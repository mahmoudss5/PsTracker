package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageResponseDto;

import java.util.List;

public interface TeamMessageService {

    TeamMessageResponseDto sendMessage(Long teamId, Long senderId, TeamMessageCreateDto dto);

    List<TeamMessageResponseDto> getTeamMessages(Long teamId, Long requesterId);
}
