package com.TrainingTracker.TraingingTracker.integration;

import com.TrainingTracker.TraingingTracker.TraingingTrackerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TraingingTrackerApplication.class)
@org.springframework.test.context.ActiveProfiles("test")
class ApplicationContextIT {

	@Test
	void contextLoads() {
	}

}
