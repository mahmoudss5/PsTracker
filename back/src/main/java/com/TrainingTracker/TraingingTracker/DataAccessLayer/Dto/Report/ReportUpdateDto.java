package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report;

public record ReportUpdateDto(
        Long numberOfProblems,
        Long numberOfHints,
        String comment
) {
}
