package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.CodeforcesUserInfo;

public interface CfService {

    boolean checkIfUserCfAccountExist(String userHandle);

    CodeforcesUserInfo getUserInfo(String userHandle);

    // Backwards-compatible method used by older tests/callers
    Long getUserRating(String userHandle);

}
