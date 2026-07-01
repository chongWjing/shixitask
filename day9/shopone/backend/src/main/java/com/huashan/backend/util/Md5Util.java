package com.huashan.backend.util;

import java.security.MessageDigest;

//工具类
public class Md5Util {

    public static String encrypt(String str) {
        try{
            MessageDigest md=MessageDigest.getInstance("MD5") ;
            byte[] bytes=md.digest(str.getBytes()) ;
            StringBuilder sb=new StringBuilder() ;
            for(byte b:bytes){
                String hex=Integer.toHexString(b&0xff) ;
                if(hex.length()==1) sb.append("0") ;
                sb.append(hex) ;
            }
            return sb.toString() ;
        }catch (Exception e){
            throw new RuntimeException("加密失败",e);
        }
    }
}
