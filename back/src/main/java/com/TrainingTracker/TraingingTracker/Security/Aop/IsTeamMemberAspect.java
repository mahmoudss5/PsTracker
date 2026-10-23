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

@Aspect
@Component
@AllArgsConstructor
public class IsTeamMemberAspect {

    private final MaterialService materialService;
    private final UserService userService;

    @Before("@annotation(com.TrainingTracker.TraingingTracker.Security.Aop.IsTeamMember)")
    public void CheckUserTeam(JoinPoint joinPoint) throws Exception {
        String s3Key = joinPoint.getArgs()[0].toString();
        Long currentUserId = SecuiryUserUtil.getCurrntUserId();

        Long materialTeam = materialService.getTeamIdByS3Key(s3Key);
        if (materialTeam == null) {
            throw new AppException("this material currently do not belond to any Team", HttpStatus.FORBIDDEN);
        }
        Boolean isSame = userService.isSameTeam(materialTeam, currentUserId);
        if (!isSame) {
            throw new AppException("you are not a member of this team", HttpStatus.FORBIDDEN);
        }
    }
}
