package com.TrainingTracker.TraingingTracker.Security.Aop.isSame.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsSameUser {

    int argumentIndex() default 0;
}
