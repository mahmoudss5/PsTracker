package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Storage;

public record StorageUploadResponse(
        String uploadUrl,
        String s3Key
) {}
