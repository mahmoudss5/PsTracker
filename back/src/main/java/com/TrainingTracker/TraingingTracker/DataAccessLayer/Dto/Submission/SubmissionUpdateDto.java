package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission;

public record SubmissionUpdateDto(
        String verdict,
        int timeConsumedMs,
        int memoryConsumedBytes
) {
}
