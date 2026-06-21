package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User;

public record TraineResponse(Long id,
                             String userName,
                             String role,
                             String email,
                             String teamName,
                             Long numberOfSolveProblems
                             ) {
}
