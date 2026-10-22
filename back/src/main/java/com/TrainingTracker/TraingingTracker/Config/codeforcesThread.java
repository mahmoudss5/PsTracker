package com.TrainingTracker.TraingingTracker.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class codeforcesThread {


    @Bean(name = "codeforcesWorker")
    public ThreadPoolTaskScheduler codeforcesWorker(){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("codeforces-worker-");
        scheduler.initialize();
        return scheduler;
    }

}
