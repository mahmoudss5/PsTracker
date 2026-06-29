package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report;

public record ReportCreateDto(
        Long userId,
        Long numberOfProblems,
        Long numberOfHints,
        String comment
) {
}
