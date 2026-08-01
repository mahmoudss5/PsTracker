package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserPresence;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Presencs {
    private final ApplicationEventPublisher publisher;

    @MessageMapping("/presence.update")
    public void updatePresence(@Payload UserPresence userPresence){
        publisher.publishEvent(userPresence);
    }
}
