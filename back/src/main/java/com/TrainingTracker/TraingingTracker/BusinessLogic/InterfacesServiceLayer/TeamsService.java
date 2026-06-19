package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

public interface TeamsService {

     public String createTeam(String teamName);

    public void JoinTeam(String teamCode);
     public void leaveTeam(Long teamId);

}
