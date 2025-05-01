package com.vi5hnu.codesprout.utils;

import jakarta.servlet.http.Cookie;

import java.util.Random;
import java.util.UUID;

public class Utils {
    public static Cookie generateCookie(String jwtToken,int jwtExpireMs,String path){
        final Cookie cookie=new Cookie("jwt", jwtToken);
//        cookie.setSecure(true);//send via https only
        cookie.setMaxAge(jwtExpireMs/1000);
        cookie.setHttpOnly(true);//no access via js
        cookie.setPath(path);
        cookie.setDomain("");
        return cookie;
    }

    public static String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a random 6-digit number
        return String.valueOf(otp);
    }

    public static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
