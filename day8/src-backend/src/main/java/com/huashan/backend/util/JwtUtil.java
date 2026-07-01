package com.huashan.backend.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//用于生成和解析JSON Web Token的工具类
public class JwtUtil {
    private static final String SECRET_KEY = "ecommerce-secret-key-2024";
    private static final long EXPIRATION = 24*60*60*1000;

    public static String generateToken(Integer userId,String username) {
        //生成JWT
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }


    public static Map<String, Object> parseToken(String token) {
        //解析JWT
        try{
            JWTVerifier verifier =JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
            DecodedJWT jwt=verifier.verify(token);
            Map<String,Object> map=new HashMap<>();
            map.put("userId",jwt.getClaim("userId").asInt());
            map.put("username",jwt.getClaim("username").asString());
            return map;
        }catch (Exception e){
            return null;
        }

    }
}
