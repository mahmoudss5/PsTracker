package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Contest.ContestResponseDto;
import java.util.List;

public interface ContestService {
    List<ContestResponseDto> getContestsByUserId(Long userId);
    List<ContestResponseDto> getCurrentUserContests();
}
