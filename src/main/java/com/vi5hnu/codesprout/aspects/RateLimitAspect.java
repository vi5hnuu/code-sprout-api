package com.vi5hnu.codesprout.aspects;

import com.vi5hnu.codesprout.annotation.RateLimit;
import com.vi5hnu.codesprout.security.RequestContext;
import com.vi5hnu.codesprout.services.RedisRateLimitService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * AOP advice that enforces {@link RateLimit} annotations using a distributed
 * Redis sliding-window rate limiter.
 *
 * Works correctly across multiple Spring Boot pods — all share the same Redis.
 * Falls back to allow-through if Redis is unavailable (fail-open).
 */
@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RedisRateLimitService rateLimitService;

    @Around("@annotation(rateLimit)")
    public Object handleRateLimit(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String identity = (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal()))
                ? auth.getName()
                : RequestContext.getIpAddress();

        long windowMs = rateLimit.unit().toMillis(rateLimit.time());

        if (rateLimitService.tryConsume(identity, rateLimit.capacity(), windowMs)) {
            return pjp.proceed();
        }
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
    }
}
