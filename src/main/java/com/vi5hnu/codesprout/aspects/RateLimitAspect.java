package com.vi5hnu.codesprout.aspects;

import com.vi5hnu.codesprout.annotation.RateLimit;
import com.vi5hnu.codesprout.services.BucketService;
import io.github.bucket4j.Bucket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private BucketService bucketService;

    @Around("@annotation(rateLimit)")
    public Object handleRateLimit(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        String userId = "anonymous";

        Bucket bucket = bucketService.resolveBucket(userId, rateLimit.capacity(), rateLimit.time(), rateLimit.unit());

        if (bucket.tryConsume(1)) {
            return pjp.proceed();
        } else {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
    }
}
