package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material;

public record MaterialCreateDto(
        String fileType,
        String title,
        String subtitle,
        String fileSize,
        String s3Key
) {}
