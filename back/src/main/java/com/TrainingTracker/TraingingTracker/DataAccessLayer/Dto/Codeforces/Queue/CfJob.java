package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue;

import java.util.UUID;

public record CfJob(
        UUID id,
        CfJobType type,
        Long userId,
        String handle,
        boolean waitForResult,
        int attempt
) {

    public static CfJob background(CfJobType type, Long userId, String handle) {
        return new CfJob(UUID.randomUUID(), type, userId, handle, false, 0);
    }

    public static CfJob request(CfJobType type, String handle) {
        return new CfJob(UUID.randomUUID(), type, null, handle, true, 0);
    }

    public CfJob nextAttempt() {
        return new CfJob(id, type, userId, handle, waitForResult, attempt + 1);
    }
}
