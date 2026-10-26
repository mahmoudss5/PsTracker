package com.TrainingTracker.TraingingTracker.Security.Aop.isSame.User;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserIdRequestDto;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AuthorizationException;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SameUserOrUserCoachAspect {

    private final UserService userService;

    public SameUserOrUserCoachAspect(UserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(sameUserOrUserCoach)")
    public void checkSameUserOrUserCoach(
            JoinPoint joinPoint,
            SameUserOrUserCoach sameUserOrUserCoach) {
        Long requestedUserId = resolveUserId(joinPoint, sameUserOrUserCoach.argumentIndex());
        Long currentUserId = SecuiryUserUtil.getCurrntUserId();

        if (!userService.isSameUserOrUserCoach(currentUserId, requestedUserId)) {
            throw new AuthorizationException(
                    "You can only access your own data or data belonging to your trainees"
            );
        }
    }

    private Long resolveUserId(JoinPoint joinPoint, int argumentIndex) {
        Object[] args = joinPoint.getArgs();
        if (argumentIndex < 0 || argumentIndex >= args.length) {
            throw new AuthorizationException("Unable to resolve the requested user");
        }

        Object argument = args[argumentIndex];
        if (argument instanceof UserIdRequestDto request) {
            return request.userId();
        }

        if (argument instanceof Number number) {
            return number.longValue();
        }

        if (argument instanceof String value) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException ignored) {
                // Return the same authorization response for invalid identifiers.
            }
        }

        throw new AuthorizationException("Unable to resolve the requested user");
    }
}
