package com.censoft.gateway.util;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * token 公用方法类
 * */

@Service
public class TokenUtil {


    final static float LOADING_FACTOR = 0.5f;

    /**
     * 获取token
     * */
    public static String getToken (String username,String TOKEN_SECRET,int duration){
        //过期时间
        long EXPIRE_DATE = duration * 60 * 1000;

        String token = "";
        try {
            //过期时间
            Supplier<Long> supplier=System::currentTimeMillis;
            Date date = new Date(supplier.get()+EXPIRE_DATE);
//            Date date = new Date(System.currentTimeMillis()+EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String,Object> header = new HashMap<>();
            header.put("typ","JWT");
            header.put("alg","RS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("accountName",username)
                    .withClaim("creatTime",System.currentTimeMillis()/1000)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
        return token;
    }

    /**
    * todo  旧的token应该加入黑名单中？
    * @Description 刷新token
    * @Parm
    * @return 一个新的token
     *
    **/
    public String flushToken(String token,String TOKEN_SECRET,int duration){
        String accountName = getInfo(token, TOKEN_SECRET);
        return getToken(accountName,TOKEN_SECRET,duration);

    }

    /**
     * 获取token内部存储的信息
     * */
    public  String getInfo(String token,String TOKEN_SECRET){
        if(token==null || token=="") return null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            return claims.get("accountName").asString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 验证token，通过返回true
     * [token]需要校验的串
     **/
    public  boolean verify(String token,String TOKEN_SECRET){
        if(token==null || token.equals("")) return false;

        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            return claims.get("accountName").asString()==null ? false: true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    /**
    * @Description 计算token是否快要过期，判断标准为 剩余时间/(当前时间-token生成时间)  > 1/2
    * @Parm token
    * @return        code = 1  小于加载因子   未过期
     *                      2  大于加载因子 小于 1   将要过期
     *                      3  大于1    token已经过期
    **/
    public int countToken(String token){
        JSONObject jsonObject = new JSONObject();
        DecodedJWT jwt = JWT.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        Claim exp = claims.get("exp");
        Long creatTokenTime = claims.get("creatTime").asLong();
        Long now = System.currentTimeMillis()/1000;
        Long expiresTime= exp.asLong();

        // 计算从生成到销毁的所有时间
        float allTime = expiresTime - creatTokenTime ;
        // 剩余时间
        float remainTime = expiresTime-now;
        // 占比
        float inie = remainTime / allTime;
        //   inie 可能是正数(小数)  负数   0
        if(inie > 0 && inie > LOADING_FACTOR){
            return 1;
        }else if(inie > 0 && inie <= LOADING_FACTOR){
            return  2;
        }
       return 3;
    }




    /**
     * 压力测试： 1kw 数据（ 以手机号为例）
     *                  获取token  为2分钟左右
     *                               开始时间Fri Oct 23 16:56:28 CST 2020
     *                               结束时间Fri Oct 23 16:58:11 CST 2020
     *                  解密token 获取用户信息  为3分钟
     *                              开始时间Fri Oct 23 16:52:44 CST 2020
     *                              结束时间Fri Oct 23 16:55:36 CST 2020
     * */
    public static void main(String[] args)  {
//        String token= getToken("13601270693","sdfsdfdsfsdfsdf",30);
        //token 就是jwt生成的token
        String token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdFRpbWUiOjE2MDcwNTA5ODEsImFjY291bnROYW1lIjoiMTM2MDEyNzA2OTMiLCJleHAiOjE2MDcwNTI3ODF9.18sxAIQPeHgex698xp38XyBf0KTDqDIpuGLtYbM27QU";
        System.out.println(token);
        DecodedJWT jwt = JWT.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        Claim exp = claims.get("exp");
        Long creatTokenTime = claims.get("creatTime").asLong();
        Long now = System.currentTimeMillis()/1000;
        Long expiresTime= exp.asLong();
        float allTime = expiresTime - creatTokenTime ;
        float remainTime = expiresTime - now;
        float inie= remainTime / allTime;

        System.out.println(inie);


//        System.out.println(getToken("13601270693"));
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(getToken("13601270693"));


//        System.out.println("开始时间"+DateUtils.getNowDate());
//        for (int i = 0; i < 10000000; i++) {
//            getToken("13601270693");
//        }
//        System.out.println("结束时间"+DateUtils.getNowDate());


//        System.out.println("开始时间"+DateUtils.getNowDate());
//
//        for (int i = 0; i < 100000; i++) {
//            verify("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MDM2MjI2MTEsInVzZXJuYW1lIjoiMTM2MDEyNzA2OTMifQ.sUFxXrX-U_5bIDB6j_8dc2mtIWnAYgwtR0bg_XHTNmA");
//        }
//        System.out.println("结束时间"+DateUtils.getNowDate());

    }
}
