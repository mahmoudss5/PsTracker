package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.User;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpUserService implements UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserServiceMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "trainees",key = "#id")
    public TraineResponse getUserResponseById(Long id) {
        return userMapper.toTraineResponse(getUserById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public TraineResponse getCurrentUser() {
        Long currentUserId = SecuiryUserUtil.getCurrntUserId();
        return getUserResponseById(currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allTrainees")
    public List<TraineResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toTraineResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "teamTrainees",key = "#teamId")
    public List<TraineResponse> getAllUserByTeamId(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
        return team.getTrainees()
                .stream()
                .map(userMapper::toTraineResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUserEntites() {
        return userRepository.findAll();
    }

}
