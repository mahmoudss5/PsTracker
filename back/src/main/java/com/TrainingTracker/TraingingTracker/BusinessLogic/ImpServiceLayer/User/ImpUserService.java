package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.User;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpUserService implements UserService {

    private final UserRepository userRepository;
   private final TeamsService teamsService;
  private final UserServiceMapper userMapper;
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));
    }

    @Override
    public List<TraineResponse> getAllUserByTeamId(Long teamId) {
        Team team=teamsService.getTeamById(teamId);
        List<TraineResponse> users = team.getTrainees()
                .stream()
                .map(userMapper::toTraineResponse)
                .toList();
                return users;
    }

}
