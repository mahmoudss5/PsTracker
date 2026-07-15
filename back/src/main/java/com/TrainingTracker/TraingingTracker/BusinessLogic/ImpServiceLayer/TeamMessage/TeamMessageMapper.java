package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.TeamMessage;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.TeamMessage;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class TeamMessageMapper {

    public TeamMessageResponseDto toDto(TeamMessage message) {
        return new TeamMessageResponseDto(
                message.getId(),
                message.getContent(),
                message.getCreatedAt().atZone(ZoneId.of("Africa/Cairo")).toOffsetDateTime(),
                message.getTeam().getId(),
                message.getSender().getId(),
                message.getSender().getUsername()
        );
    }
}
