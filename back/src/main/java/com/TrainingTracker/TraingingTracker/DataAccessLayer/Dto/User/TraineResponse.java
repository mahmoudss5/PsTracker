package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User;

public record TraineResponse(Long id,
                             String userName,
                             String role,
                             String email,
                             Long teamId,
                             String teamName,
                             Long numberOfSolveProblems,
                             Long totalSumbissions,
                             Long numberOfTimeLimitVerdict,
                             Long numberOfMemoryLimitVerdict,
                             Long numberOfWrongAnswerVerdict
                             ) {
}
