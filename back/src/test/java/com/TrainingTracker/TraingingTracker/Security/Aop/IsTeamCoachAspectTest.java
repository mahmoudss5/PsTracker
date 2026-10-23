package com.TrainingTracker.TraingingTracker.Security.Aop;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.MaterialService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsTeamCoachAspectTest {

    @Mock
    private TeamsService teamsService;

    @Mock
    private MaterialService materialService;

    @Test
    void allowsCoachToUploadToOwnedTeam() {
        IsTeamCoachAspect aspect = new IsTeamCoachAspect(teamsService, materialService);
        JoinPoint joinPoint = joinPoint("generateUploadUrl", 42L, "file.pdf", "application/pdf", "1024");

        when(teamsService.getTeamCoachId(42L)).thenReturn(7L);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);

            assertDoesNotThrow(() -> aspect.checkUserTeam(joinPoint));
        }
    }

    @Test
    void rejectsCoachFromAnotherTeam() {
        IsTeamCoachAspect aspect = new IsTeamCoachAspect(teamsService, materialService);
        JoinPoint joinPoint = joinPoint("generateUploadUrl", 42L, "file.pdf", "application/pdf", "1024");

        when(teamsService.getTeamCoachId(42L)).thenReturn(8L);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);

            AppException exception = assertThrows(AppException.class, () -> aspect.checkUserTeam(joinPoint));

            assertEquals(403, exception.getStatus().value());
        }
    }

    @Test
    void allowsCoachToDeleteFileOwnedByTheirTeam() {
        IsTeamCoachAspect aspect = new IsTeamCoachAspect(teamsService, materialService);
        JoinPoint joinPoint = joinPoint("deleteFile", "teams/42/file.pdf");

        when(materialService.getTeamIdByS3Key("teams/42/file.pdf")).thenReturn(42L);
        when(teamsService.getTeamCoachId(42L)).thenReturn(7L);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);

            assertDoesNotThrow(() -> aspect.checkUserTeam(joinPoint));
        }
    }

    @Test
    void rejectsCoachFromDeletingFileOwnedByAnotherTeam() {
        IsTeamCoachAspect aspect = new IsTeamCoachAspect(teamsService, materialService);
        JoinPoint joinPoint = joinPoint("deleteFile", "teams/42/file.pdf");

        when(materialService.getTeamIdByS3Key("teams/42/file.pdf")).thenReturn(42L);
        when(teamsService.getTeamCoachId(42L)).thenReturn(8L);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);

            AppException exception = assertThrows(AppException.class, () -> aspect.checkUserTeam(joinPoint));

            assertEquals(403, exception.getStatus().value());
        }
    }

    private JoinPoint joinPoint(String methodName, Object... args) {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.getName()).thenReturn(methodName);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(args);
        return joinPoint;
    }
}
