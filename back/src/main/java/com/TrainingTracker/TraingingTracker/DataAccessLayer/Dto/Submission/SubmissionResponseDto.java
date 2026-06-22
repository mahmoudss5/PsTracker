package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission;

import java.time.LocalDateTime;

public record SubmissionResponseDto(
        Long id,
        Long userId,
        String userName,
        Long problemId,
        String problemName,
        String verdict,
        int timeConsumedMs,
        int memoryConsumedBytes,
        LocalDateTime createdAt
) {
}
