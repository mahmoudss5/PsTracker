package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.CfService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.CodeforcesUserInfo;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJob;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJobType;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.CfQueueException;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.CfUserHandlerError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpCfService implements CfService {

    private final UserRepository userRepository;
    private final CfQueue cfQueue;

    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    private void enqueueContestSyncJobs() {
        enqueueJobs(CfJobType.CONTEST_SYNC);
    }

    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.MINUTES)
    private void enqueueSubmissionSyncJobs() {
        enqueueJobs(CfJobType.SUBMISSION_SYNC);
    }

    @Override
    public boolean checkIfUserCfAccountExist(String userHandle) {
        try {
            return getUserInfo(userHandle) != null;
        } catch (CfUserHandlerError exception) {
            return false;
        }
    }

    @Override
    public CodeforcesUserInfo getUserInfo(String userHandle) {
        try {
            return cfQueue.enqueueAndWait(CfJobType.USER_INFO, userHandle, CodeforcesUserInfo.class);
        } catch (CfQueueException exception) {
            if ("CLIENT".equals(exception.errorType())) {
                throw new CfUserHandlerError("User not found on Codeforces");
            }
            throw new IllegalStateException("couldn't connect to codeforces", exception);
        }
    }

    @Override
    public Long getUserRating(String userHandle) {
        CodeforcesUserInfo info = getUserInfo(userHandle);
        return info == null ? null : info.rating();
    }

    private void enqueueJobs(CfJobType type) {
        log.info("Queueing Codeforces {} jobs...", type);
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String handle = user.getCodeforcesHandle();
            if (handle == null || handle.isBlank()) {
                continue;
            }

            cfQueue.enqueue(CfJob.background(type, user.getId(), handle));
        }
    }
}
