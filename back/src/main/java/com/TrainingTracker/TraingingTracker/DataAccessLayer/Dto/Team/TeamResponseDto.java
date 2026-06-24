package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import java.util.List;

public record TeamResponseDto(
        Long id,
        String teamName,
        String teamCode,
        Long coachId,
        String coachUsername,
        List<TraineResponse> trainees
) {}
