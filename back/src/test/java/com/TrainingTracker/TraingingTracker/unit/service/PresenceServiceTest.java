package com.TrainingTracker.TraingingTracker.unit.service;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Presence.PresenceService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserPresence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresenceServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> values;

    @Mock
    private SimpMessagingTemplate broker;

    @InjectMocks
    private PresenceService presenceService;

    @Test
    void refreshingPresenceStoresOneMinuteTtlAndBroadcastsCurrentUsers() {
        when(redisTemplate.opsForValue()).thenReturn(values);
        when(redisTemplate.keys("presence:*")).thenReturn(Set.of("presence:7"));

        presenceService.refreshUserPresence(new UserPresence(7L));

        verify(values).set("presence:7", "online", Duration.ofMinutes(1));
        verify(broker).convertAndSend("/topic/presence", List.of(new UserPresence(7L)));
    }

    @Test
    void noRedisKeysMeansNoOnlineUsers() {
        when(redisTemplate.keys("presence:*")).thenReturn(null);

        assertTrue(presenceService.getAllOnlineUserIds().isEmpty());
    }

    @Test
    void parsesOnlineUserIdsFromRedisKeys() {
        when(redisTemplate.keys("presence:*")).thenReturn(Set.of("presence:7", "presence:9"));

        assertEquals(Set.of(7L, 9L), Set.copyOf(presenceService.getAllOnlineUserIds()));
    }

    @Test
    void scheduledBroadcastIncludesEmptyPresenceList() {
        when(redisTemplate.keys("presence:*")).thenReturn(Set.of());

        presenceService.SendCurrentPresence();

        verify(broker).convertAndSend("/topic/presence", List.of());
    }
}
