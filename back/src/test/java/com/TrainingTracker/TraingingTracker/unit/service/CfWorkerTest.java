package com.TrainingTracker.TraingingTracker.unit.service;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces.CfQueue;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces.CfWorker;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Problem.ProblemMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Submission.SubmissionMapper;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.CodeforcesUserInfo;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJob;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJobType;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.RatingChange.CodeforcesRatingChangeResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.RatingChange.CodeforcesRatingChangeResult;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.codeforcesSubmissionDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.result.CodeforcesProblem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.result.CodeforcesSubmissionResult;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Contest;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ContestRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ProblemRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.SubmissionRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CfWorkerTest {

    @Mock
    private CfQueue cfQueue;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProblemMapper problemMapper;

    @Mock
    private SubmissionMapper submissionMapper;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private ContestRepository contestRepository;

    @InjectMocks
    private CfWorker worker;

    @Test
    void processDoesNothingWhenQueueIsEmpty() {
        when(cfQueue.dequeue()).thenReturn(null);

        worker.process();

        verifyNoInteractions(restClient);
    }

    @Test
    void userInfoJobReturnsCodeforcesProfile() {
        CfJob job = CfJob.request(CfJobType.USER_INFO, "handle");
        when(cfQueue.dequeue()).thenReturn(job);
        JsonNode body = new ObjectMapper().readTree("""
                {"status":"OK","result":[{"rating":1200,"maxRating":1300,"rank":"Newbie","maxRank":"Pupil"}]}
                """);
        when(restClient.get().uri("/user.info?handles={handle}&checkHistoricHandles=false", "handle")
                .retrieve().toEntity(JsonNode.class)).thenReturn(ResponseEntity.ok(body));

        worker.process();

        ArgumentCaptor<Object> result = ArgumentCaptor.forClass(Object.class);
        verify(cfQueue).complete(eq(job), result.capture());
        CodeforcesUserInfo info = assertInstanceOf(CodeforcesUserInfo.class, result.getValue());
        assertEquals(1200L, info.rating());
        assertEquals(1300L, info.maxRate());
        assertEquals("Newbie", info.rank());
    }

    @Test
    void invalidUserInfoResponseFailsAsClientError() {
        CfJob job = CfJob.request(CfJobType.USER_INFO, "missing");
        when(cfQueue.dequeue()).thenReturn(job);
        JsonNode body = new ObjectMapper().readTree("""
                {"status":"FAILED","result":[]}
                """);
        when(restClient.get().uri("/user.info?handles={handle}&checkHistoricHandles=false", "missing")
                .retrieve().toEntity(JsonNode.class)).thenReturn(ResponseEntity.ok(body));

        worker.process();

        verify(cfQueue).fail(job, "CLIENT", "User not found on Codeforces");
        verify(cfQueue, never()).complete(eq(job), any());
    }

    @Test
    void rateLimitedJobIsRequeuedWithNextAttempt() {
        CfJob job = CfJob.request(CfJobType.USER_INFO, "handle");
        when(cfQueue.dequeue()).thenReturn(job);
        when(restClient.get().uri("/user.info?handles={handle}&checkHistoricHandles=false", "handle")
                .retrieve().toEntity(JsonNode.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        worker.process();

        verify(cfQueue).requeue(job.nextAttempt());
        verify(cfQueue, never()).fail(eq(job), any(), any());
    }

    @Test
    void rateLimitedJobFailsAfterMaximumAttempts() {
        CfJob job = new CfJob(java.util.UUID.randomUUID(), CfJobType.USER_INFO, null, "handle", true, 3);
        when(cfQueue.dequeue()).thenReturn(job);
        when(restClient.get().uri("/user.info?handles={handle}&checkHistoricHandles=false", "handle")
                .retrieve().toEntity(JsonNode.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        worker.process();

        verify(cfQueue).fail(job, "CLIENT", "Codeforces rate limit exceeded");
        verify(cfQueue, never()).requeue(any());
    }

    @Test
    void serverFailureIsRetried() {
        CfJob job = CfJob.request(CfJobType.USER_INFO, "handle");
        when(cfQueue.dequeue()).thenReturn(job);
        when(restClient.get().uri("/user.info?handles={handle}&checkHistoricHandles=false", "handle")
                .retrieve().toEntity(JsonNode.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        worker.process();

        verify(cfQueue).requeue(job.nextAttempt());
    }

    @Test
    void contestSyncSavesOnlyNewContests() {
        CfJob job = CfJob.background(CfJobType.CONTEST_SYNC, 7L, "handle");
        User user = User.builder().id(7L).build();
        when(cfQueue.dequeue()).thenReturn(job);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        CodeforcesRatingChangeResult newContest = new CodeforcesRatingChangeResult();
        newContest.setContestId(100L);
        newContest.setContestName("Round 100");
        newContest.setRatingUpdateTimeSeconds(1_700_000_000L);
        CodeforcesRatingChangeResult existingContest = new CodeforcesRatingChangeResult();
        existingContest.setContestId(200L);
        existingContest.setRatingUpdateTimeSeconds(1_700_000_000L);
        CodeforcesRatingChangeResponse response = new CodeforcesRatingChangeResponse();
        response.setStatus("OK");
        response.setResult(List.of(newContest, existingContest));
        when(restClient.get().uri("/user.rating?handle={handle}", "handle")
                .retrieve().body(CodeforcesRatingChangeResponse.class)).thenReturn(response);
        when(contestRepository.existsByUserIdAndContestId(7L, 100L)).thenReturn(false);
        when(contestRepository.existsByUserIdAndContestId(7L, 200L)).thenReturn(true);

        worker.process();

        ArgumentCaptor<Contest> saved = ArgumentCaptor.forClass(Contest.class);
        verify(contestRepository).save(saved.capture());
        assertSame(user, saved.getValue().getUser());
        assertEquals(100L, saved.getValue().getContestId());
        assertEquals("Round 100", saved.getValue().getContestName());
        assertEquals(LocalDateTime.ofEpochSecond(1_700_000_000L, 0, java.time.ZoneOffset.UTC),
                saved.getValue().getRatingUpdateTime());
        verify(cfQueue).complete(job, null);
    }

    @Test
    void submissionSyncCreatesMissingProblemAndSubmission() {
        CfJob job = CfJob.background(CfJobType.SUBMISSION_SYNC, 7L, "handle");
        User user = User.builder().id(7L).build();
        CodeforcesProblem codeforcesProblem = CodeforcesProblem.builder().name("Watermelon").build();
        CodeforcesSubmissionResult result = CodeforcesSubmissionResult.builder()
                .id(500L).problem(codeforcesProblem).build();
        codeforcesSubmissionDto response = codeforcesSubmissionDto.builder()
                .status("OK").result(List.of(result)).build();
        Problem mappedProblem = Problem.builder().name("Watermelon").build();
        Problem persistedProblem = Problem.builder().id(9L).name("Watermelon").build();
        Submission mappedSubmission = Submission.builder().id(10L).build();
        when(cfQueue.dequeue()).thenReturn(job);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(restClient.get().uri("/user.status?handle={handle}&from=1&count=100", "handle")
                .retrieve().body(codeforcesSubmissionDto.class)).thenReturn(response);
        when(problemMapper.ToEntity(codeforcesProblem)).thenReturn(mappedProblem);
        when(problemRepository.findByName("Watermelon")).thenReturn(Optional.empty());
        when(problemRepository.save(mappedProblem)).thenReturn(persistedProblem);
        when(submissionMapper.toEntity(result, user, persistedProblem)).thenReturn(mappedSubmission);

        worker.process();

        verify(problemRepository).save(mappedProblem);
        verify(submissionRepository).save(mappedSubmission);
        verify(cfQueue).complete(job, null);
    }

    @Test
    void submissionSyncSkipsAlreadySavedSubmission() {
        CfJob job = CfJob.background(CfJobType.SUBMISSION_SYNC, 7L, "handle");
        User user = User.builder().id(7L).build();
        CodeforcesProblem codeforcesProblem = CodeforcesProblem.builder().name("Watermelon").build();
        CodeforcesSubmissionResult result = CodeforcesSubmissionResult.builder()
                .id(500L).problem(codeforcesProblem).build();
        codeforcesSubmissionDto response = codeforcesSubmissionDto.builder()
                .status("OK").result(List.of(result)).build();
        Problem persistedProblem = Problem.builder().id(9L).name("Watermelon").build();
        when(cfQueue.dequeue()).thenReturn(job);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(restClient.get().uri("/user.status?handle={handle}&from=1&count=100", "handle")
                .retrieve().body(codeforcesSubmissionDto.class)).thenReturn(response);
        when(problemMapper.ToEntity(codeforcesProblem)).thenReturn(persistedProblem);
        when(problemRepository.findByName("Watermelon")).thenReturn(Optional.of(persistedProblem));
        when(submissionRepository.existsByCodeforcesSubmissionId(500L)).thenReturn(true);

        worker.process();

        verify(problemRepository, never()).save(any());
        verify(submissionRepository, never()).save(any());
        verify(cfQueue).complete(job, null);
    }
}
