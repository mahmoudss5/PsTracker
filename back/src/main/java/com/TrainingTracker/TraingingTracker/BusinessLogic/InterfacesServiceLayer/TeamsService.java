package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;

import java.util.List;

public interface TeamsService {

     public String createTeam(String teamName);

     public void JoinTeam(String teamCode);
     public void leaveTeam(Long teamId);

     public Team getTeamById(Long teamId);

     public TeamResponseDto getTeamResponseById(Long teamId);

     public List<TeamResponseDto> getCurrentCoachTeams();
     void updateTeamName(Long teamId, String teamName);
}
