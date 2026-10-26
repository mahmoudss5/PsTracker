package com.TrainingTracker.TraingingTracker.Security.Aop.isSame.Team;

import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.MaterialService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;

import java.util.Objects;

@AllArgsConstructor
@Component
@Aspect
public class IsTeamCoachAspect {

    private final TeamsService teamsService;
    private final MaterialService materialService;

    @Before("@annotation(com.TrainingTracker.TraingingTracker.Security.Aop.isSame.Team.IsTeamCoach)")
    public void checkUserTeam(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Long userId = SecuiryUserUtil.getCurrntUserId();

        boolean valid = switch (methodName) {
            case "generateUploadUrl" -> {
                Long teamId = (Long) joinPoint.getArgs()[0];
                yield checkTeamCoachByTeamId(teamId, userId);
            }
            case "deleteFile" -> {
                String s3Key = (String) joinPoint.getArgs()[0];
                yield checkTeamCoachByS3Key(s3Key, userId);
            }
            default -> false;
        };

        if (!valid) {
            throw new AppException("you are not a coach of this team", HttpStatus.FORBIDDEN);
        }
    }

    private Boolean checkTeamCoachByS3Key(String s3Key, Long userId) {
        Long teamId = materialService.getTeamIdByS3Key(s3Key);
        return checkTeamCoachByTeamId(teamId, userId);
    }

    private Boolean checkTeamCoachByTeamId(Long teamId, Long userId) {
        Long coachId = teamsService.getTeamCoachId(teamId);
        return Objects.equals(coachId, userId);
    }

}
