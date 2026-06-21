package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import java.util.List;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;

public interface UserService {

    public User getUserById(Long id);
    public List<TraineResponse> getAllUserByTeamId(Long teamId);
    
    
}
