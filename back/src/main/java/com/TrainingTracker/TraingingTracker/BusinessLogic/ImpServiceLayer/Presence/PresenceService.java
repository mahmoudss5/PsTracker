package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Presence;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserPresence;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String PRESENCE_KEY_PREFIX = "presence:";
    private static final Duration PRESENCE_TTL = Duration.ofMinutes(1);

    @EventListener
    public void refreshUserPresence(UserPresence userPresence) {
        String key = PRESENCE_KEY_PREFIX + userPresence.userId();
        redisTemplate.opsForValue().set(key, "online", PRESENCE_TTL);
    }

    public List<Long> getAllOnlineUserIds() {
        Set<String> keys = redisTemplate.keys(PRESENCE_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = new ArrayList<>();
        for (String key : keys) {
            String idPart = key.substring(PRESENCE_KEY_PREFIX.length());
            userIds.add(Long.valueOf(idPart));
        }
        return userIds;
    }

    @Scheduled(fixedRate = 10000, timeUnit = TimeUnit.MILLISECONDS)
    public void SendCurrentPresence() {
        List<Long> onlineUserIds = getAllOnlineUserIds();
        List<UserPresence> userPresences = new ArrayList<>();
        for (Long userId : onlineUserIds) {
            userPresences.add(new UserPresence(userId));
        }
        simpMessagingTemplate.convertAndSend("/topic/presence", userPresences);
    }

}
