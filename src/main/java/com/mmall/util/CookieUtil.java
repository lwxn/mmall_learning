package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".happymmall.com";// 一级域名
    private final static String COOKIE_NAME = "mmall_login_token";

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for(Cookie ck : cookies){
                log.info("request for cookie name : {}, value : {}",ck.getName(),ck.getValue());
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    log.info("return cookie name : {}, value : {}",ck.getName(),ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie ck = new Cookie(COOKIE_NAME,token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/"); // 说明在/下，也就是根目录下的cookie都可以得到保存

        // 单位是秒
        // 如果不设置过期时间的话，是不会写入硬盘的，只会写在内存，只在当前页面有效
        ck.setMaxAge(60 * 60 * 24 * 365); // 存个一年先
        log.info("write cookie name : {}, value : {}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie ck : cookies){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0); // 设置为0，然后返回给浏览器，浏览器看到0就会删除这个cookie
                    log.info("del cookie name : {}, value : {}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                }
            }
        }
    }
}
