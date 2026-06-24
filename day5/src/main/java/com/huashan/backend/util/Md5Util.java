package com.huashan.backend.util;

import java.security.MessageDigest;

//MD5加密
public class Md5Util {


    //对输入的字符进行MD5加密
    public static String encrypt(String str) {
       try{
           MessageDigest md=MessageDigest.getInstance("MD5");
           byte[] bytes=md.digest(str.getBytes());

           StringBuilder sb=new StringBuilder();
           for(byte b:bytes){
               int i=b&0xff;
               String hex=Integer.toHexString(i);
               if(hex.length()==1){
                  sb.append("0");
               }
               sb.append(hex);
           }
           return sb.toString();

       }catch (Exception e){
           throw new RuntimeException(e);
       }
    }
}
