package com.censoft.gateway.fiflt;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.censoft.common.utils.ServletUtils;
import com.censoft.gateway.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.censoft.common.constant.Constants;
import com.censoft.common.core.domain.R;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

/**
 * 网关鉴权
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered
{
    // 排除过滤的 uri 地址
    // swagger排除自行添加
    private static final String[]  whiteList = {
            "/dfs/**",
            "/exInterface/generalUser/**",
            "/exInterface/organUser/**",
            "/otherlogin/info/**",
            "/userRegister/userRegister/**",
            "/userRegister/orgenRegister/**",
            "/userRegister/forgetPassword/**",
            "/otherlogin/otherApi/**",
            "/exInterface/wechat/**",
            "/exInterface/wechatBind/**",
            "/otherlogin/otherApi/**",
            "/otherlogin/wxlogin/**"
    };
    // 初步优化白名单 数据结构更换为 map
    private static final Map  whiteMap;
    static {
        whiteMap = new HashMap();
        whiteMap.put("/otherlogin/token/countToken","/otherlogin/token/countToken");
        whiteMap.put("/auth/login","/auth/login");
        whiteMap.put("/personallogin/login/proLogin","/personallogin/login/proLogin");
        whiteMap.put("/otherlogin/orgenUserlogin/orgLogin","/otherlogin/orgenUserlogin/orgLogin");
        whiteMap.put( "/backmanage/caUserLogin/caLogin", "/backmanage/caUserLogin/caLogin");
        whiteMap.put("/backmanage/caUserLogin/caAdminLogin","/backmanage/caUserLogin/caAdminLogin");
        whiteMap.put("/backmanage/userInterface/certTwoData","/backmanage/userInterface/certTwoData");
        whiteMap.put("/user/register","/user/register");
        whiteMap.put("/system/v2/api-docs","/system/v2/api-docs");
        whiteMap.put("/personallogin/login/loginOut","/personallogin/login/loginOut");
        whiteMap.put("/exInterface/exInterface/getTokenInfo","/exInterface/exInterface/getTokenInfo");
        whiteMap.put("/exInterface/systemApi/loginOut","/exInterface/systemApi/loginOut");
    }


    @Autowired
    private TokenUtil tokenUtil;

    @Value("${TOKEN.TOKEN_SECRET}")
    public  String TOKEN_SECRET;

    @Value("${TOKEN.TIME}")
    public  String TOKEN_TIME;


    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> ops;

    /**
    * @Description 系统网关过滤器
     * 1. 获取请求地址，校验在白名单是否存在，存在直接return
     * 2. 不存在 获取header中的token,校验token的有效性，根据token解析相应的用户信息，存到chain中，供后续方法获取使用
    * @Parm
    * @return
    **/
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        String url = exchange.getRequest().getURI().getPath();
        if(whiteMap.get(url) != null){
            return chain.filter(exchange);
        }
        // 跳过不需要验证的路径
        List<String> urlList = Arrays.asList(whiteList);
        //白名单处理
        for (String filUrl:urlList) {
            if(filUrl.endsWith("**") && url.startsWith(filUrl.replace("**","")))
                return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst(Constants.TOKEN);
        // token为空
        if (StringUtils.isBlank(token))
        {
            return setUnauthorizedResponse(exchange,401, "token can't null or empty string");
        }

        //用户ID
        String userId=null;
        //用户名称
        String userName=null;
        //用户类型（用来区分内部用户、分中心用户、教育处用户）
        String userType=null;
        // 新token 用来前端刷新token
        String newToken = null;

        ServerHttpResponse response = exchange.getResponse();

        //判断是不是注册系统打来的请求
        if(token.indexOf("REGISTER") == -1 ){
            String userStr = ops.get(Constants.ACCESS_TOKEN + token);
            JSONObject jo = JSONObject.parseObject(userStr);
            userId = jo.getString("userId");
            // 查询token信息
            if (StringUtils.isBlank(userId))
            {
                return setUnauthorizedResponse(exchange,401, "token verify error");
            }
            userName=jo.getString("loginName");
            userType=jo.getString("userType");

        }else{
            token = token.replace(Constants.REGISTER_TOKEN, "");
            //查询token是不是在黑名单中
            if(ops.get(Constants.BLACK_TOKEN + token) != null){
                return setUnauthorizedResponse(exchange,401, "token is destroy");
            }
            //校验token是否有效 ？ 快过期 ？ start
            int register_token = tokenUtil.countToken(token.replace("REGISTER_TOKEN_", ""));

            if(register_token == 3 ) return setUnauthorizedResponse(exchange,401, "token verify error");
            //设置token
            if(register_token == 2){
                newToken = tokenUtil.flushToken(token,TOKEN_SECRET,Integer.valueOf(TOKEN_TIME));
                response.getHeaders().add("newToken",newToken);
            }
            //校验token是否有效 ？ 快过期 ？ end
            String username = tokenUtil.getInfo(token, TOKEN_SECRET);
            if(username == null) return setUnauthorizedResponse(exchange,401, "token is destroy");
            String userStr = ops.get(username);
            JSONObject jo = JSONObject.parseObject(userStr);
            userId = jo.getString("userId");
            userName=username;
            userType=jo.getString("userType");
        }

        // 设置userId到request里，后续根据userId，获取用户信息
        ServerHttpRequest mutableReq = exchange.getRequest().mutate()
                                        .header(Constants.CURRENT_ID, userId)
                                        .header(Constants.CURRENT_USERNAME, userName).header("userType",userType)
                                        .build() ;
        ServerWebExchange mutableExchange = exchange.mutate()
                .request(mutableReq).build();
        mutableExchange.mutate().response(response).build();
        return chain.filter(mutableExchange);
    }

    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange,int code, String msg)
    {
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] response = null;
        try
        {
            response = JSON.toJSONString(R.error(401, msg)).getBytes(Constants.UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
        return originalResponse.writeWith(Flux.just(buffer));
    }

    @Override
    public int getOrder()
    {
        return -200;
    }
}
