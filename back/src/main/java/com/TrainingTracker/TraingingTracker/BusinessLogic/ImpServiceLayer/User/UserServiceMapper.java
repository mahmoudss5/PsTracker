package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.User;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceMapper {

      private final TeamRepository teamRepository;

    public TraineResponse toTraineResponse(User user) {

        String teamName=teamRepository.findTeamNameByUserId(user.getId());

        TraineResponse traineResponse=new TraineResponse(
               user.getId(),
              user.getUsername(),
              user.getRole().name(),
              user.getEmail(),
              teamName,
                0L
      );
        return traineResponse;
    }
}
