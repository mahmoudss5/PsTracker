package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

public interface CfService {

    boolean checkIfUserCfAccountExist(String userHandle);
   
    Long getUserRating(String userHandle);
    
}
