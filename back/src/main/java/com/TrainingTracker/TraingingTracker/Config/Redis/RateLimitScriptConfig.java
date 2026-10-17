package com.TrainingTracker.TraingingTracker.Config.Redis;

import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

@Configuration
public class RateLimitScriptConfig {


    @Bean
    public RedisScript<List> slidingWindowScript(){
        DefaultRedisScript<List> script =
                new DefaultRedisScript<>();

        script.setLocation(
                new ClassPathResource("scripts/slidingWindow.lua")
        );

        script.setResultType(List.class);

        return script;
    }

}
