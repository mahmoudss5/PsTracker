package com.TrainingTracker.TraingingTracker.Security.Aop;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserIdRequestDto;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AuthorizationException;
import com.TrainingTracker.TraingingTracker.Security.Aop.isSame.User.SameUserOrUserCoach;
import com.TrainingTracker.TraingingTracker.Security.Aop.isSame.User.SameUserOrUserCoachAspect;
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
class SameUserOrUserCoachAspectTest {

    @Mock
    private UserService userService;

    @Mock
    private SameUserOrUserCoach annotation;

    @Test
    void allowsTheAuthenticatedUser() {
        SameUserOrUserCoachAspect aspect = new SameUserOrUserCoachAspect(userService);
        when(annotation.argumentIndex()).thenReturn(0);
        when(userService.isSameUserOrUserCoach(7L, 7L)).thenReturn(true);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);
            assertDoesNotThrow(() -> aspect.checkSameUserOrUserCoach(
                    joinPoint(new UserIdRequestDto(7L)), annotation));
        }
    }

    @Test
    void allowsTheCoachToViewATrainee() {
        SameUserOrUserCoachAspect aspect = new SameUserOrUserCoachAspect(userService);
        when(annotation.argumentIndex()).thenReturn(0);
        when(userService.isSameUserOrUserCoach(7L, 8L)).thenReturn(true);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);
            assertDoesNotThrow(() -> aspect.checkSameUserOrUserCoach(
                    joinPoint(new UserIdRequestDto(8L)), annotation));
        }
    }

    @Test
    void rejectsAnUnrelatedUser() {
        SameUserOrUserCoachAspect aspect = new SameUserOrUserCoachAspect(userService);
        when(annotation.argumentIndex()).thenReturn(0);
        when(userService.isSameUserOrUserCoach(7L, 8L)).thenReturn(false);

        try (MockedStatic<SecuiryUserUtil> securityUserUtil = mockStatic(SecuiryUserUtil.class)) {
            securityUserUtil.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);
            AuthorizationException exception = assertThrows(
                    AuthorizationException.class,
                    () -> aspect.checkSameUserOrUserCoach(
                            joinPoint(new UserIdRequestDto(8L)), annotation)
            );
            assertEquals(403, exception.getStatus().value());
        }
    }

    private JoinPoint joinPoint(Object... args) {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(args);
        return joinPoint;
    }
}
