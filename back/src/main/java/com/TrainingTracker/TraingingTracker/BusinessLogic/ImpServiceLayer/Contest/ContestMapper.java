package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Contest;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Contest.ContestResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Contest;
import org.springframework.stereotype.Component;

@Component
public class ContestMapper {
    public ContestResponseDto toDto(Contest entity) {
        if (entity == null) {
            return null;
        }
        return new ContestResponseDto(
                entity.getId(),
                entity.getContestId(),
                entity.getContestName(),
                entity.getRank(),
                entity.getOldRating(),
                entity.getNewRating(),
                entity.getRatingUpdateTime()
        );
    }
}
