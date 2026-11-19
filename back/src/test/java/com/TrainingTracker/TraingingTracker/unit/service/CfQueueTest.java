package com.TrainingTracker.TraingingTracker.unit.service;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces.CfQueue;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJob;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJobResult;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJobType;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.CfQueueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CfQueueTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ListOperations<String, String> list;

    private final ObjectMapper mapper = new ObjectMapper();
    private CfQueue queue;

    @BeforeEach
    void setUp() {
        queue = new CfQueue(redisTemplate, mapper);
    }

    @Test
    void interactiveRequestIsPushedToInteractiveQueue() {
        when(redisTemplate.opsForList()).thenReturn(list);
        CfJob job = CfJob.request(CfJobType.USER_INFO, "handle");

        queue.enqueue(job);

        ArgumentCaptor<String> serialized = ArgumentCaptor.forClass(String.class);
        verify(list).rightPush(eq("codeforces:interactive-requests"), serialized.capture());
        assertEquals(job, mapper.readValue(serialized.getValue(), CfJob.class));
    }

    @Test
    void backgroundRequestIsPushedToBackgroundQueue() {
        when(redisTemplate.opsForList()).thenReturn(list);
        CfJob job = CfJob.background(CfJobType.CONTEST_SYNC, 7L, "handle");

        queue.enqueue(job);

        ArgumentCaptor<String> serialized = ArgumentCaptor.forClass(String.class);
        verify(list).leftPush(eq("codeforces:requests"), serialized.capture());
        assertEquals(job, mapper.readValue(serialized.getValue(), CfJob.class));
    }

    @Test
    void retryGoesToBackOfItsQueue() {
        when(redisTemplate.opsForList()).thenReturn(list);
        CfJob job = CfJob.background(CfJobType.SUBMISSION_SYNC, 7L, "handle").nextAttempt();

        queue.requeue(job);

        ArgumentCaptor<String> serialized = ArgumentCaptor.forClass(String.class);
        verify(list).rightPush(eq("codeforces:requests"), serialized.capture());
        assertEquals(1, mapper.readValue(serialized.getValue(), CfJob.class).attempt());
    }

    @Test
    void dequeuePrefersInteractiveRequests() {
        when(redisTemplate.opsForList()).thenReturn(list);
        CfJob job = CfJob.request(CfJobType.USER_INFO, "handle");
        when(list.rightPop("codeforces:interactive-requests")).thenReturn(mapper.writeValueAsString(job));

        assertEquals(job, queue.dequeue());

        verify(list, never()).rightPop("codeforces:requests");
    }

    @Test
    void dequeueFallsBackToBackgroundAndReturnsNullWhenEmpty() {
        when(redisTemplate.opsForList()).thenReturn(list);
        CfJob job = CfJob.background(CfJobType.CONTEST_SYNC, 7L, "handle");
        when(list.rightPop("codeforces:interactive-requests")).thenReturn(null);
        when(list.rightPop("codeforces:requests"))
                .thenReturn(mapper.writeValueAsString(job), (String) null);

        assertEquals(job, queue.dequeue());
        assertNull(queue.dequeue());
    }

    @Test
    void requestReturnsDeserializedResponse() {
        when(redisTemplate.opsForList()).thenReturn(list);
        String response = mapper.writeValueAsString(new CfJobResult(true, mapper.writeValueAsString("OK"), null, null));
        when(list.rightPop(startsWith("codeforces:responses:"), eq(Duration.ofSeconds(30))))
                .thenReturn(response);

        assertEquals("OK", queue.enqueueAndWait(CfJobType.USER_INFO, "handle", String.class));

        ArgumentCaptor<String> queued = ArgumentCaptor.forClass(String.class);
        verify(list).rightPush(eq("codeforces:interactive-requests"), queued.capture());
        CfJob job = mapper.readValue(queued.getValue(), CfJob.class);
        verify(list).rightPop("codeforces:responses:" + job.id(), Duration.ofSeconds(30));
    }

    @Test
    void requestTimesOutWhenNoResponseArrives() {
        when(redisTemplate.opsForList()).thenReturn(list);

        CfQueueException error = assertThrows(CfQueueException.class,
                () -> queue.enqueueAndWait(CfJobType.USER_INFO, "handle", String.class));

        assertEquals("TIMEOUT", error.errorType());
    }

    @Test
    void requestPropagatesWorkerFailure() {
        when(redisTemplate.opsForList()).thenReturn(list);
        String response = mapper.writeValueAsString(new CfJobResult(false, null, "NOT_FOUND", "No such handle"));
        when(list.rightPop(startsWith("codeforces:responses:"), eq(Duration.ofSeconds(30))))
                .thenReturn(response);

        CfQueueException error = assertThrows(CfQueueException.class,
                () -> queue.enqueueAndWait(CfJobType.USER_INFO, "handle", String.class));

        assertEquals("NOT_FOUND", error.errorType());
        assertEquals("No such handle", error.getMessage());
    }

    @Test
    void completePublishesResultWithOneMinuteExpiry() {
        when(redisTemplate.opsForList()).thenReturn(list);
        CfJob job = new CfJob(UUID.randomUUID(), CfJobType.USER_INFO, null, "handle", true, 0);

        queue.complete(job, "OK");

        ArgumentCaptor<String> resultJson = ArgumentCaptor.forClass(String.class);
        String key = "codeforces:responses:" + job.id();
        verify(list).rightPush(eq(key), resultJson.capture());
        verify(redisTemplate).expire(key, Duration.ofMinutes(1));
        CfJobResult result = mapper.readValue(resultJson.getValue(), CfJobResult.class);
        assertTrue(result.success());
        assertEquals("OK", mapper.readValue(result.payload(), String.class));
    }

    @Test
    void failPublishesErrorForInteractiveRequest() {
        when(redisTemplate.opsForList()).thenReturn(list);
        CfJob job = CfJob.request(CfJobType.USER_INFO, "handle");

        queue.fail(job, "API_ERROR", "unavailable");

        ArgumentCaptor<String> resultJson = ArgumentCaptor.forClass(String.class);
        verify(list).rightPush(eq("codeforces:responses:" + job.id()), resultJson.capture());
        CfJobResult result = mapper.readValue(resultJson.getValue(), CfJobResult.class);
        assertFalse(result.success());
        assertEquals("API_ERROR", result.errorType());
        assertEquals("unavailable", result.errorMessage());
    }

    @Test
    void backgroundCompletionDoesNotCreateResponseKey() {
        CfJob job = CfJob.background(CfJobType.CONTEST_SYNC, 7L, "handle");

        queue.complete(job, "ignored");
        queue.fail(job, "API_ERROR", "ignored");

        verify(redisTemplate, never()).opsForList();
    }
}
