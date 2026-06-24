package com.huashan.backend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.HashMap;
import java.util.Map;

//主要作用是token的生成和解析
public class JwtUtil {

    //JWT签名密钥
    private static final String SECRET = "huashan";
    //JWT过期时间
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;//24小时


    //生成JWT
    public static String generateToken(Integer userId,String username) {
        //生成JWT
        //jWt.create()
        return JWT.create()
                //自定义声明
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + EXPIRATION))
                .sign(Algorithm.HMAC256(SECRET));
    }

    //解析JWT
    public static Map<String, Object>  parseToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", jwt.getClaim("userId").asInt());
            map.put("username", jwt.getClaim("username").asString());
            return map;
        }catch (Exception e){
            return  null;
        }
    }
}
