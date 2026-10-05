package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material;

import java.time.LocalDateTime;

public record MaterialResponseDto(
        Long id,
        String fileType,
        String title,
        String subtitle,
        String fileSize,
        String s3Key,
        LocalDateTime createdAt
) {}
