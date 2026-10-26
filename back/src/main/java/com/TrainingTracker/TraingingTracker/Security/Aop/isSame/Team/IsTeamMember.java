package com.TrainingTracker.TraingingTracker.Security.Aop.isSame.Team;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsTeamMember {

    Resource resource() default Resource.TEAM_ID;

    int argumentIndex() default 0;

    enum Resource {
        TEAM_ID,
        S3_KEY
    }
}
