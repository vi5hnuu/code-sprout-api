package com.vi5hnu.codesprout.security;

public class RequestContext {

    private static final ThreadLocal<String> ipAddressThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> userAgentThreadLocal = new ThreadLocal<>();

    // Methods to set values in ThreadLocal
    public static void setIpAddress(String ipAddress) {
        ipAddressThreadLocal.set(ipAddress);
    }

    public static void setUserAgent(String userAgent) {
        userAgentThreadLocal.set(userAgent);
    }

    public static String getIpAddress() {
        return ipAddressThreadLocal.get();
    }

    public static String getUserAgent() {
        return userAgentThreadLocal.get();
    }

    // Method to clear the ThreadLocal variables after request processing
    public static void clear() {
        ipAddressThreadLocal.remove();
        userAgentThreadLocal.remove();
    }
}

