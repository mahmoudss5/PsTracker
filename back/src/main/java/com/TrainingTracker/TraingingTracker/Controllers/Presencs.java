package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserPresence;
import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class Presencs {
    private final ApplicationEventPublisher publisher;

    @MessageMapping("/presence.update")
    public void updatePresence(@Payload(required = false) UserPresence ignoredPresence,
                               Principal principal){
        Authentication authentication = principal instanceof Authentication auth ? auth : null;
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new org.springframework.security.access.AccessDeniedException("Authentication required");
        }
        publisher.publishEvent(new UserPresence(securityUser.getId()));
    }
}
