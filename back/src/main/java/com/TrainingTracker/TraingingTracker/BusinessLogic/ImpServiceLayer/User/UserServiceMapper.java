package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.User;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.SubmissionRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceMapper {

    private final TeamRepository teamRepository;
    private final SubmissionRepository submissionRepository;

    public TraineResponse toTraineResponse(User user) {
        String teamName = teamRepository.findTeamNameByUserId(user.getId());
        Long solvedProblemsCount = submissionRepository.countSolvedProblemsByUserId(user.getId());

        return new TraineResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getEmail(),
                teamName,
                solvedProblemsCount
        );
    }
}
