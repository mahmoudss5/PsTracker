package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Submission;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Problem.ProblemMapper;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.result.CodeforcesSubmissionResult;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class SubmissionMapper {

    private final UserRepository userRepository;
    private final ProblemMapper problemMapper;
    public Submission toEntity(SubmissionCreateDto dto, User user, Problem problem) {
        return Submission.builder()
                .user(user)
                .problem(problem)
                .verdict(dto.verdict())
                .timeConsumedMs(dto.timeConsumedMs())
                .memoryConsumedBytes(dto.memoryConsumedBytes())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateEntity(Submission submission, SubmissionUpdateDto dto) {
        submission.setVerdict(dto.verdict());
        submission.setTimeConsumedMs(dto.timeConsumedMs());
        submission.setMemoryConsumedBytes(dto.memoryConsumedBytes());
    }

    public SubmissionResponseDto toDto(Submission submission) {
        User user = submission.getUser();
        Problem problem = submission.getProblem();
        return new SubmissionResponseDto(
                submission.getId(),
                user == null ? null : user.getId(),
                user == null ? null : user.getUsername(),
                problem == null ? null : problem.getId(),
                problem == null ? null : problem.getName(),
                submission.getVerdict(),
                submission.getTimeConsumedMs(),
                submission.getMemoryConsumedBytes(),
                submission.getCreatedAt()
        );
    }

    private LocalDateTime toLocalDateTime(Long epochSecond) {
        LocalDateTime submissionTime = Instant
                .ofEpochSecond(epochSecond)
                .atZone(ZoneId.of("Africa/Cairo"))
                .toLocalDateTime();
        return submissionTime;
    }

    public Submission toEntity(CodeforcesSubmissionResult codeforcesSubmissionResult, User user, Problem problem) {
        Submission submission=Submission.builder()
                .codeforcesSubmissionId(codeforcesSubmissionResult.getId())
                .user(user)
                .problem(problem)
                .verdict(codeforcesSubmissionResult.getVerdict())
                .timeConsumedMs(codeforcesSubmissionResult.getTimeConsumedMillis())
                .memoryConsumedBytes(codeforcesSubmissionResult.getMemoryConsumedBytes())
                .createdAt(toLocalDateTime(codeforcesSubmissionResult.getCreationTimeSeconds()))
                .build();
        return submission;
    }
}
