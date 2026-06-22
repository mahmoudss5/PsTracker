package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem;

import java.util.List;

public record ProblemResponseDto(
        Long id,
        String problemIndex,
        String name,
        Long contestId,
        Integer rating,
        List<String> tags
) {
}
