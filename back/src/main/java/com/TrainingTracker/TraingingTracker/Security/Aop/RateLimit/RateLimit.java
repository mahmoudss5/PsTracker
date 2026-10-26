package com.TrainingTracker.TraingingTracker.Security.Aop.RateLimit;

import com.TrainingTracker.TraingingTracker.Security.RateLimitIdentity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int requests();

    int windowSeconds();

    String key() default "";

    RateLimitIdentity identity()
            default RateLimitIdentity.USER;
}
