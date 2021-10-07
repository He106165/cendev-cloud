package com.censoft.gateway.fiflt;

import com.alibaba.fastjson.JSON;
import com.censoft.common.constant.Constants;
import com.censoft.common.core.domain.R;
import com.censoft.common.utils.StringUtils;
import com.censoft.gateway.util.SensitivewordGain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * 敏感词过滤器
 */
@Slf4j
@Component
public class SensitivewordFilter implements GlobalFilter, Ordered {
    //默认敏感词过滤器时关闭的
    private boolean flag = false;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (flag == false){
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
         //获取所有的请求参数
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        StringBuilder stringBuilder = new StringBuilder();
         //遍历map获取所有的请求参数
        for (String key: queryParams.keySet()) {
            //拼接获取到的字符串
            stringBuilder.append(queryParams.get(key));
        }


        SensitivewordGain filter = new SensitivewordGain();
        //获取敏感词
        Set<String> set = filter.getSensitiveWord(stringBuilder.toString(), 1);
        //如果敏感词的大小为0的话
        if (set.size()==0){
            return chain.filter(exchange);
        }else{
            return setUnauthorizedResponse(exchange,502, "参数中不能出现敏感词"+set.toString());
        }
    }
    //获取返回的code,msg和exchange
    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange,int code, String msg)
    {
        ServerHttpResponse originalResponse = exchange.getResponse();
        // 找不到敏感词的 http状态码，占用了502 badgateway
        originalResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] response = null;
        try
        {
            response = JSON.toJSONString(R.error(code, msg)).getBytes(Constants.UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
        return originalResponse.writeWith(Flux.just(buffer));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
