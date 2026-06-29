package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report;

import java.time.LocalDateTime;

public record ReportResponseDto(
        Long id,
        Long userId,
        String userName,
        Long numberOfProblems,
        Long numberOfHints,
        String comment,
        LocalDateTime createdAt
) {
}
