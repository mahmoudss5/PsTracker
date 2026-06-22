package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import java.util.List;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;

public interface UserService {

    User getUserById(Long id);
    TraineResponse getUserResponseById(Long id);
    TraineResponse getCurrentUser();
    List<TraineResponse> getAllUsers();
    List<TraineResponse> getAllUserByTeamId(Long teamId);
    List<User>getAllUserEntites();
    
    
}
