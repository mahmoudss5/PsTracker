package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJob;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJobResult;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJobType;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.CfQueueException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CfQueue {

    private static final String BACKGROUND_QUEUE = "codeforces:requests";
    private static final String INTERACTIVE_QUEUE = "codeforces:interactive-requests";
    private static final String RESPONSE_PREFIX = "codeforces:responses:";
    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration RESPONSE_TTL = Duration.ofMinutes(1);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void enqueue(CfJob job) {
        String queue = job.waitForResult() ? INTERACTIVE_QUEUE : BACKGROUND_QUEUE;
        if (job.waitForResult()) {
            redisTemplate.opsForList().rightPush(queue, serialize(job));
        } else {
            redisTemplate.opsForList().leftPush(queue, serialize(job));
        }
    }

    public void requeue(CfJob job) {
        String queue = job.waitForResult() ? INTERACTIVE_QUEUE : BACKGROUND_QUEUE;
        redisTemplate.opsForList().rightPush(queue, serialize(job));
    }

    public CfJob dequeue() {
        String value = redisTemplate.opsForList().rightPop(INTERACTIVE_QUEUE);
        if (value == null) {
            value = redisTemplate.opsForList().rightPop(BACKGROUND_QUEUE);
        }
        return value == null ? null : deserialize(value, CfJob.class);
    }

    public <T> T enqueueAndWait(CfJobType type, String handle, Class<T> resultType) {
        CfJob job = CfJob.request(type, handle);
        enqueue(job);

        String response = redisTemplate.opsForList()
                .rightPop(responseKey(job), RESPONSE_TIMEOUT);

        if (response == null) {
            throw new CfQueueException("TIMEOUT", "Timed out waiting for Codeforces response");
        }

        CfJobResult result = deserialize(response, CfJobResult.class);
        if (!result.success()) {
            throw new CfQueueException(result.errorType(), result.errorMessage());
        }

        return deserialize(result.payload(), resultType);
    }

    public void complete(CfJob job, Object payload) {
        if (!job.waitForResult()) {
            return;
        }

        publishResult(job, new CfJobResult(true, serialize(payload), null, null));
    }

    public void fail(CfJob job, String errorType, String message) {
        if (!job.waitForResult()) {
            return;
        }

        publishResult(job, new CfJobResult(false, null, errorType, message));
    }

    private void publishResult(CfJob job, CfJobResult result) {
        String key = responseKey(job);
        redisTemplate.opsForList().rightPush(key, serialize(result));
        redisTemplate.expire(key, RESPONSE_TTL);
    }

    private String responseKey(CfJob job) {
        return RESPONSE_PREFIX + job.id();
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new CfQueueException("SERIALIZATION", "Could not serialize Codeforces queue message");
        }
    }

    private <T> T deserialize(String value, Class<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (Exception exception) {
            throw new CfQueueException("SERIALIZATION", "Could not deserialize Codeforces queue message");
        }
    }
}
