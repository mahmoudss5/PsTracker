package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Problem.ProblemMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Submission.SubmissionMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.CfService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.codeforcesSubmissionDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.result.CodeforcesSubmissionResult;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ProblemRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.SubmissionRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpCfService implements CfService {

    private final RestClient restClient;
    private final UserRepository userRepository;
    private final ProblemMapper problemMapper;
    private final SubmissionMapper submissionMapper;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ContestRepository contestRepository;


    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    private void syncCodeforcesUserData() {
        log.info("Starting Codeforces user contest data synchronization...");
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            return;
        }

        for (User user : userList) {
            if (user.getCodeforcesHandle() == null || user.getCodeforcesHandle().isEmpty()) {
                continue;
            }

            try {
                com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.RatingChange.CodeforcesRatingChangeResponse response = restClient.get()
                        .uri("/user.rating?handle={handle}", user.getCodeforcesHandle())
                        .retrieve()
                        .body(com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.RatingChange.CodeforcesRatingChangeResponse.class);

                if (response != null && "OK".equals(response.getStatus()) && response.getResult() != null) {
                    for (com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.RatingChange.CodeforcesRatingChangeResult result : response.getResult()) {
                        if (!contestRepository.existsByUserIdAndContestId(user.getId(), result.getContestId())) {
                            com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Contest contest = com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Contest.builder()
                                    .contestId(result.getContestId())
                                    .contestName(result.getContestName())
                                    .rank(result.getRank())
                                    .oldRating(result.getOldRating())
                                    .newRating(result.getNewRating())
                                    .ratingUpdateTime(java.time.LocalDateTime.ofEpochSecond(result.getRatingUpdateTimeSeconds(), 0, java.time.ZoneOffset.UTC))
                                    .user(user)
                                    .build();
                            contestRepository.save(contest);
                        }
                    }
                }
            } catch (HttpClientErrorException e) {
                log.error("User not found on Codeforces for contest sync: {}", e.getMessage());
            } catch (HttpServerErrorException e) {
                log.error("Couldn't connect to Codeforces for contest sync: {}", e.getMessage());
            } catch (Exception e) {
                log.error("Error during Codeforces contest sync: {}", e.getMessage(), e);
            }

            try {
                Thread.sleep(2000); // codeforces rate limits
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.MINUTES)
    private void syncCodforcesSubmissionData() {
        log.info("Starting Codeforces submission data synchronization...");
        List<User>userList=userRepository.findAll();
        if(userList.isEmpty()){
            log.error("No users found in the database");
            return;
        }
        for(User user:userList) {
            String username = user.getUsername();
           log.info("Fetching submissions for user: {}", username);
            try {
                codeforcesSubmissionDto response = restClient.get()
                        .uri("/user.status?handle={handle}&from=1&count=100", getUserCodeforecesHandle(user.getId()))
                        .retrieve()
                        .body(codeforcesSubmissionDto.class);

                if(!response.getStatus().equals("OK")){
                    log.error("Failed to fetch submissions for user {}: {}", user.getId(), response.getStatus());
                    continue;
                }
            List<CodeforcesSubmissionResult> submissions = response.getResult();
            for (CodeforcesSubmissionResult submission : submissions) {

                Problem problem = problemMapper.ToEntity(submission.getProblem());

                Problem dbProblem = problemRepository.findByName(problem.getName())
                        .orElseGet(() -> problemRepository.save(problem));

                if(!submissionRepository.existsByCodeforcesSubmissionId(submission.getId())){
                    Submission submissionEntity = submissionMapper.toEntity(submission, user, dbProblem);
                    submissionRepository.save(submissionEntity);
                }
            }

            } catch (HttpClientErrorException e) {
                log.error("User not found on Codeforces: {}", e.getMessage());
            } catch (HttpServerErrorException e) {
                log.error("Couldn't connect to Codeforces: {}", e.getMessage());
            }
              // sleep for 3 seconds before the next request (Codeforces API rate limit)
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.error("Thread interrupted during sleep: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }


    @Override
    public boolean checkIfUserCfAccountExist(String userHandle) {
        try {
            ResponseEntity<JsonNode> response = restClient.get()
                    .uri("/user.info?handles={handle}&checkHistoricHandles=false", userHandle)
                    .retrieve()
                    .toEntity(JsonNode.class);

            return response.getStatusCode().is2xxSuccessful()
                    && "OK".equals(response.getBody().get("status").asText());
        } catch (HttpClientErrorException e) {
            return false;
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("couldn't connect to codeforces");
        }
    }

    @Override
    public Long getUserRating(String userHandle) {
         try {
            ResponseEntity<JsonNode> response = restClient.get()
                    .uri("/user.info?handles={handle}&checkHistoricHandles=false", userHandle)
                    .retrieve()
                    .toEntity(JsonNode.class);

            if (response.getStatusCode().is2xxSuccessful() && "OK".equals(response.getBody().get("status").asText())) {
                JsonNode userInfo = response.getBody().get("result").get(0);
                return userInfo.has("rating") ? userInfo.get("rating").asLong() : 0L;
            } else {
                throw new RuntimeException("Failed to fetch user rating from Codeforces");
            }
         }catch (HttpClientErrorException e) {
             log.error("User not found on Codeforces: {}", e.getMessage());
            throw new RuntimeException("User not found on Codeforces");
        } catch (HttpServerErrorException e) {
             log.error("Couldn't connect to Codeforces: {}", e.getMessage());
             throw new RuntimeException("couldn't connect to codeforces");
         }
    }
    private String getUserCodeforecesHandle(Long userId) {
        return userRepository.findById(userId).orElseThrow().getCodeforcesHandle();
    }


}
