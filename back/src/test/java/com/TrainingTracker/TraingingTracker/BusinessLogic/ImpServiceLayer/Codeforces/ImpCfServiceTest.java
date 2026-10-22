package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.CodeforcesUserInfo;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJobType;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.CfQueueException;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.CfUserHandlerError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpCfServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CfQueue cfQueue;

    @InjectMocks
    private ImpCfService cfService;

    @Test
    void checkIfUserCfAccountExist_ReturnsTrueForValidUser() {
        String handle = "tourist";
        CodeforcesUserInfo info = new CodeforcesUserInfo(3500L, 3800L, "legend", "legend");

        when(cfQueue.enqueueAndWait(eq(CfJobType.USER_INFO), eq(handle), eq(CodeforcesUserInfo.class)))
                .thenReturn(info);

        assertTrue(cfService.checkIfUserCfAccountExist(handle));
    }

    @Test
    void checkIfUserCfAccountExist_ReturnsFalseForInvalidUser() {
        String handle = "nonexistent";

        when(cfQueue.enqueueAndWait(eq(CfJobType.USER_INFO), eq(handle), eq(CodeforcesUserInfo.class)))
                .thenThrow(new CfQueueException("CLIENT", "User not found"));

        assertFalse(cfService.checkIfUserCfAccountExist(handle));
    }

    @Test
    void getUserInfo_ThrowsWhenCodeforcesIsUnavailable() {
        String handle = "tourist";

        when(cfQueue.enqueueAndWait(eq(CfJobType.USER_INFO), eq(handle), eq(CodeforcesUserInfo.class)))
                .thenThrow(new CfQueueException("SERVER", "Codeforces unavailable"));

        assertThrows(IllegalStateException.class, () -> cfService.getUserInfo(handle));
    }

    @Test
    void getUserRating_ReturnsRatingFromQueuedUserInfoRequest() {
        String handle = "tourist";
        CodeforcesUserInfo info = new CodeforcesUserInfo(3500L, 3800L, "legend", "legend");

        when(cfQueue.enqueueAndWait(eq(CfJobType.USER_INFO), eq(handle), eq(CodeforcesUserInfo.class)))
                .thenReturn(info);

        assertEquals(3500L, cfService.getUserRating(handle));
    }
}
