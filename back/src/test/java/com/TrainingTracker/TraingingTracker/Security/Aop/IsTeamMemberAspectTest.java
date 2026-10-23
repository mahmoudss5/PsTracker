package com.TrainingTracker.TraingingTracker.Security.Aop;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.MaterialService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.aspectj.lang.JoinPoint;
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
class IsTeamMemberAspectTest {

    @Mock
    private MaterialService materialService;

    @Mock
    private UserService userService;

    @Test
    void allowsMemberByTeamId() {
        IsTeamMemberAspect aspect = new IsTeamMemberAspect(materialService, userService);
        IsTeamMember annotation = annotation(IsTeamMember.Resource.TEAM_ID, 0);
        JoinPoint joinPoint = joinPoint(42L);
        when(userService.isSameTeam(42L, 7L)).thenReturn(true);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);
            assertDoesNotThrow(() -> aspect.checkUserTeam(joinPoint, annotation));
        }
    }

    @Test
    void rejectsNonMemberByTeamIdWithForbidden() {
        IsTeamMemberAspect aspect = new IsTeamMemberAspect(materialService, userService);
        IsTeamMember annotation = annotation(IsTeamMember.Resource.TEAM_ID, 0);
        JoinPoint joinPoint = joinPoint(42L);
        when(userService.isSameTeam(42L, 7L)).thenReturn(false);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);
            AppException exception = assertThrows(AppException.class,
                    () -> aspect.checkUserTeam(joinPoint, annotation));
            assertEquals(403, exception.getStatus().value());
        }
    }

    @Test
    void resolvesMemberByMaterialS3Key() {
        IsTeamMemberAspect aspect = new IsTeamMemberAspect(materialService, userService);
        IsTeamMember annotation = annotation(IsTeamMember.Resource.S3_KEY, 0);
        JoinPoint joinPoint = joinPoint("teams/42/material.pdf");
        when(materialService.getTeamIdByS3Key("teams/42/material.pdf")).thenReturn(42L);
        when(userService.isSameTeam(42L, 7L)).thenReturn(true);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);
            assertDoesNotThrow(() -> aspect.checkUserTeam(joinPoint, annotation));
        }
    }

    private IsTeamMember annotation(IsTeamMember.Resource resource, int argumentIndex) {
        IsTeamMember annotation = mock(IsTeamMember.class);
        when(annotation.resource()).thenReturn(resource);
        when(annotation.argumentIndex()).thenReturn(argumentIndex);
        return annotation;
    }

    private JoinPoint joinPoint(Object... args) {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(args);
        return joinPoint;
    }
}
