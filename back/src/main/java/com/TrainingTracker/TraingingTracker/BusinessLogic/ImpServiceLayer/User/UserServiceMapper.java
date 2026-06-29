package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.User;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceMapper {

    private final SubmissionRepository submissionRepository;

    public TraineResponse toTraineResponse(User user) {
        Team team = user.getTraineeTeam();
        Long teamId = team == null ? null : team.getId();
        String teamName = team == null ? null : team.getTeamName();
        Long solvedProblemsCount = submissionRepository.countSolvedProblemsByUserId(user.getId());
        java.util.List<Submission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        Long totalSubmissions = (long) submissions.size();
        Long timeLimitVerdicts = countVerdicts(submissions, "TIME_LIMIT_EXCEEDED", "TLE");
        Long memoryLimitVerdicts = countVerdicts(submissions, "MEMORY_LIMIT_EXCEEDED", "MLE");
        Long wrongAnswerVerdicts = countVerdicts(submissions, "WRONG_ANSWER", "WA");

        return new TraineResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getEmail(),
                teamId,
                teamName,
                solvedProblemsCount,
                totalSubmissions,
                timeLimitVerdicts,
                memoryLimitVerdicts,
                wrongAnswerVerdicts
        );
    }

    private Long countVerdicts(java.util.List<Submission> submissions, String... verdicts) {
        java.util.Set<String> accepted = java.util.Set.of(verdicts);
        return submissions.stream()
                .filter(submission -> submission.getVerdict() != null)
                .filter(submission -> accepted.contains(submission.getVerdict().toUpperCase()))
                .count();
    }
}
