package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Team;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.User.UserServiceMapper;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamMapper {

    private final UserServiceMapper userServiceMapper;

    public TeamResponseDto toDto(Team team) {
        if (team == null) {
            return null;
        }
        Long coachId = team.getCoach() != null ? team.getCoach().getId() : null;
        String coachUsername = team.getCoach() != null ? team.getCoach().getUsername() : null;
        List<com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse> trainees = team.getTrainees() != null 
                ? team.getTrainees().stream().map(userServiceMapper::toTraineResponse).toList() 
                : List.of();

        return new TeamResponseDto(
                team.getId(),
                team.getTeamName(),
                team.getTeamCode(),
                coachId,
                coachUsername,
                trainees
        );
    }
}
