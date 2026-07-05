package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material;

import java.time.LocalDateTime;

public record MaterialResponseDto(
        Long id,
        String kind,
        String title,
        String subtitle,
        String size,
        LocalDateTime createdAt
) {}
