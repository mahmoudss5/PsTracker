package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;

public interface TeamsService {

     public String createTeam(String teamName);

    public void JoinTeam(String teamCode);
     public void leaveTeam(Long teamId);

     public Team getTeamById(Long teamId);

}
