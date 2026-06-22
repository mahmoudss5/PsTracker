package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission;

public record SubmissionCreateDto(
        Long userId,
        Long problemId,
        String verdict,
        int timeConsumedMs,
        int memoryConsumedBytes
) {
}
