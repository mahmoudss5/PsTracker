package com.TrainingTracker.TraingingTracker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
@EnableCaching
@EnableScheduling
public class TraingingTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraingingTrackerApplication.class, args);
	}

}
