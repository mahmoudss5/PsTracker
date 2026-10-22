package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue;

public record CfJobResult(
        boolean success,
        String payload,
        String errorType,
        String errorMessage
) {
}
