package com.gci.schedule.driverless.util;

import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * request工具类
 *
 * @author pdl
 */
public class HttpRequestUtil {

    private static final String NGINX_IP_HEADER = "X-Real-IP";
    private static final String NGINX_URL_HEADER = "X-Real-Url";
    private static final String NGINX_X_FORWARDED_FOR = "C-Forwarded-For";

    /**
     * 判断用户是否登录
     */
    public static boolean isLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session.getAttribute("userId") != null;
    }

    /**
     * 获取session中当前登陆用户的organId
     */
    public static String getOrganId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String organId = session.getAttribute("organId").toString();
        return organId;
    }

    /**
     * 获取session中当前登陆用户的userId
     */
    public static String getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object userId=session.getAttribute("userId");
        if(userId==null)
        	return null;
        return userId.toString();
    }

    /**
     * 获取session中当前登陆用户的sessionId
     */
    public static String getSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionId = session.getAttribute("sessionId").toString();
        return sessionId;
    }

    /**
     * 获取ip（兼容nginx转发）
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String ips = request.getHeader(NGINX_X_FORWARDED_FOR);
        String[] ipArray = StringUtils.split(ips, ",");
        if (ipArray != null && ipArray.length > 0) {
            return StringUtils.trim(ipArray[0]);
        } else {
            String ip = request.getHeader(NGINX_IP_HEADER);
            if (StringUtils.isEmpty(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        }
    }

    /**
     * 从request中抽取当前url(兼容nginx转发模式)
     */
    public static String getRemoteUrl(HttpServletRequest request) {
        if (checkParamNull(request)) {
            return null;
        }
        String url = request.getHeader(NGINX_URL_HEADER);
        if (StringUtils.isEmpty(url)) {
            return request.getRequestURL().toString();
        } else {
            return url;
        }
    }

    /**
     * 获取hostname
     */
    public static String getHostName(HttpServletRequest request) {
        String host = request.getHeader("Host");
        return host;
    }

    /**
     * 获取用户代理
     */
    public static String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent;
    }

    private static boolean checkParamNull(Object... params) {
        for (Object param : params) {
            if (null == param) {
                return true;
            }
        }
        return false;
    }
}
