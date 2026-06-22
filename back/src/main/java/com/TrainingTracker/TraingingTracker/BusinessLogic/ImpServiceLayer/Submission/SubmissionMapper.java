package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Submission;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubmissionMapper {

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
}
