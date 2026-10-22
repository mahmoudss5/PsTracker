package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Problem.ProblemMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Submission.SubmissionMapper;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.CodeforcesUserInfo;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.RatingChange.CodeforcesRatingChangeResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.RatingChange.CodeforcesRatingChangeResult;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJob;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJobType;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.codeforcesSubmissionDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.result.CodeforcesSubmissionResult;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Contest;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ContestRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ProblemRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.SubmissionRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.CfUserHandlerError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CfWorker {

    private static final int MAX_RETRIES = 3;

    private final CfQueue cfQueue;
    private final RestClient restClient;
    private final UserRepository userRepository;
    private final ProblemMapper problemMapper;
    private final SubmissionMapper submissionMapper;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final ContestRepository contestRepository;

    @Scheduled(
            fixedDelay = 2,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "codeforcesWorker"
    )
    public void process() {
        CfJob job = cfQueue.dequeue();
        if (job == null) {
            return;
        }

        try {
            Object result = switch (job.type()) {
                case USER_INFO -> fetchUserInfo(job.handle());
                case CONTEST_SYNC -> {
                    syncContestData(job);
                    yield null;
                }
                case SUBMISSION_SYNC -> {
                    syncSubmissionData(job);
                    yield null;
                }
            };

            cfQueue.complete(job, result);
        } catch (CfUserHandlerError exception) {
            log.warn("Codeforces rejected job {}: {}", job.id(), exception.getMessage());
            cfQueue.fail(job, "CLIENT", exception.getMessage());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().value() == 429) {
                retryOrFail(job, "CLIENT", "Codeforces rate limit exceeded");
            } else {
                cfQueue.fail(job, "CLIENT", exception.getMessage());
            }
        } catch (HttpServerErrorException | ResourceAccessException exception) {
            retryOrFail(job, "SERVER", exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error while processing Codeforces job {}", job.id(), exception);
            cfQueue.fail(job, "INTERNAL", exception.getMessage());
        }
    }

    private CodeforcesUserInfo fetchUserInfo(String handle) {
        ResponseEntity<JsonNode> response = restClient.get()
                .uri("/user.info?handles={handle}&checkHistoricHandles=false", handle)
                .retrieve()
                .toEntity(JsonNode.class);

        JsonNode body = response.getBody();
        if (body == null || !response.getStatusCode().is2xxSuccessful()
                || !"OK".equals(text(body.get("status")))) {
            throw new CfUserHandlerError("User not found on Codeforces");
        }

        JsonNode result = body.get("result");
        if (result == null || !result.isArray() || result.size() == 0) {
            throw new CfUserHandlerError("User not found on Codeforces");
        }

        JsonNode userInfo = result.get(0);
        return new CodeforcesUserInfo(
                optionalLong(userInfo, "rating"),
                optionalLong(userInfo, "maxRating"),
                optionalText(userInfo, "rank"),
                optionalText(userInfo, "maxRank")
        );
    }

    private void syncContestData(CfJob job) {
        User user = getUser(job);
        CodeforcesRatingChangeResponse response = restClient.get()
                .uri("/user.rating?handle={handle}", job.handle())
                .retrieve()
                .body(CodeforcesRatingChangeResponse.class);

        if (response == null || !"OK".equals(response.getStatus()) || response.getResult() == null) {
            throw new CfUserHandlerError("Could not fetch contest data from Codeforces");
        }

        for (CodeforcesRatingChangeResult result : response.getResult()) {
            if (!contestRepository.existsByUserIdAndContestId(user.getId(), result.getContestId())) {
                contestRepository.save(Contest.builder()
                        .contestId(result.getContestId())
                        .contestName(result.getContestName())
                        .rank(result.getRank())
                        .oldRating(result.getOldRating())
                        .newRating(result.getNewRating())
                        .ratingUpdateTime(LocalDateTime.ofEpochSecond(
                                result.getRatingUpdateTimeSeconds(),
                                0,
                                ZoneOffset.UTC
                        ))
                        .user(user)
                        .build());
            }
        }
    }

    private void syncSubmissionData(CfJob job) {
        User user = getUser(job);
        codeforcesSubmissionDto response = restClient.get()
                .uri("/user.status?handle={handle}&from=1&count=100", job.handle())
                .retrieve()
                .body(codeforcesSubmissionDto.class);

        if (response == null || !"OK".equals(response.getStatus()) || response.getResult() == null) {
            throw new CfUserHandlerError("Could not fetch submissions from Codeforces");
        }

        List<CodeforcesSubmissionResult> submissions = response.getResult();
        for (CodeforcesSubmissionResult submission : submissions) {
            Problem problem = problemMapper.ToEntity(submission.getProblem());
            Problem dbProblem = problemRepository.findByName(problem.getName())
                    .orElseGet(() -> problemRepository.save(problem));

            if (!submissionRepository.existsByCodeforcesSubmissionId(submission.getId())) {
                Submission submissionEntity = submissionMapper.toEntity(submission, user, dbProblem);
                submissionRepository.save(submissionEntity);
            }
        }
    }

    private User getUser(CfJob job) {
        if (job.userId() == null) {
            throw new IllegalArgumentException("Codeforces background job has no user id");
        }
        return userRepository.findById(job.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + job.userId()));
    }

    private void retryOrFail(CfJob job, String errorType, String message) {
        if (job.attempt() < MAX_RETRIES) {
            log.warn("Retrying Codeforces job {} (attempt {})", job.id(), job.attempt() + 1);
            cfQueue.requeue(job.nextAttempt());
        } else {
            cfQueue.fail(job, errorType, message);
        }
    }

    private String text(JsonNode node) {
        return node == null || node.isNull() ? null : node.asText();
    }

    private Long optionalLong(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asLong() : null;
    }

    private String optionalText(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }
}
