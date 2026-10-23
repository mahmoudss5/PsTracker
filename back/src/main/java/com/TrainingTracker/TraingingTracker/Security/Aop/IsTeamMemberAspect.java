package com.TrainingTracker.TraingingTracker.Security.Aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.MaterialService;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;

import java.util.Objects;

@Aspect
@Component
@AllArgsConstructor
public class IsTeamMemberAspect {

    private final MaterialService materialService;
    private final UserService userService;

    @Before("@annotation(teamMember)")
    public void checkUserTeam(JoinPoint joinPoint, IsTeamMember teamMember) {
        Long currentUserId = SecuiryUserUtil.getCurrntUserId();
        Long teamId = resolveTeamId(joinPoint, teamMember);

        if (!Objects.equals(Boolean.TRUE, userService.isSameTeam(teamId, currentUserId))) {
            throw new AppException("you are not a member of this team", HttpStatus.FORBIDDEN);
        }
    }

    private Long resolveTeamId(JoinPoint joinPoint, IsTeamMember teamMember) {
        Object[] args = joinPoint.getArgs();
        int argumentIndex = teamMember.argumentIndex();
        if (argumentIndex < 0 || argumentIndex >= args.length) {
            throw new AppException("Unable to resolve the team for this request", HttpStatus.FORBIDDEN);
        }

        Object argument = args[argumentIndex];
        if (teamMember.resource() == IsTeamMember.Resource.S3_KEY) {
            if (!(argument instanceof String s3Key) || s3Key.isBlank()) {
                throw new AppException("Unable to resolve the material team", HttpStatus.FORBIDDEN);
            }
            Long teamId = materialService.getTeamIdByS3Key(s3Key);
            if (teamId == null || teamId <= 0) {
                throw new AppException("This material does not belong to a team", HttpStatus.FORBIDDEN);
            }
            return teamId;
        }

        if (argument instanceof Number number) {
            return number.longValue();
        }
        if (argument instanceof String value) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException ignored) {
                // Convert invalid team identifiers into the same authorization response.
            }
        }
        throw new AppException("Unable to resolve the team for this request", HttpStatus.FORBIDDEN);
    }
}
