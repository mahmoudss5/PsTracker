package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Contest;

import java.time.LocalDateTime;

public record ContestResponseDto(
        Long id,
        Long contestId,
        String contestName,
        Integer rank,
        Integer oldRating,
        Integer newRating,
        LocalDateTime ratingUpdateTime
) {}
