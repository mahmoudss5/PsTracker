package com.TrainingTracker.TraingingTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
public class TraingingTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraingingTrackerApplication.class, args);
	}

}
