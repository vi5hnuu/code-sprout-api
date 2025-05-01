package com.vi5hnu.codesprout.security.filters;

import com.vi5hnu.codesprout.security.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Integer.MIN_VALUE)
public class RequestInfoFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpRequest,@NonNull HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String ipAddress = getIpAddress(httpRequest);
        RequestContext.setIpAddress(ipAddress);

        // Extract the User-Agent
        String userAgent = httpRequest.getHeader("User-Agent");
        RequestContext.setUserAgent(userAgent);

        // Proceed with the next filter in the chain
        chain.doFilter(httpRequest, response);
    }

    @Override
    public void destroy() {
        // Clean up thread-local variables
        RequestContext.clear();
    }

    private String getIpAddress(HttpServletRequest request) {
        // Handle proxy and non-proxy scenarios for getting the real IP
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
