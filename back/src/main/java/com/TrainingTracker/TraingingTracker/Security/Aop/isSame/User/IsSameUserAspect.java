package com.TrainingTracker.TraingingTracker.Security.Aop.isSame.User;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserIdRequestDto;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AuthorizationException;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class IsSameUserAspect {

    @Before("@annotation(isSameUser)")
    public void checkSameUser(JoinPoint joinPoint, IsSameUser isSameUser) {
        Long requestedUserId = resolveUserId(joinPoint, isSameUser.argumentIndex());
        Long currentUserId = SecuiryUserUtil.getCurrntUserId();

        if (!Objects.equals(currentUserId, requestedUserId)) {
            throw new AuthorizationException("You can only access your own user data");
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
