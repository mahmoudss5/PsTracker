package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.User;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.CfService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UpdateProfileDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UpdatePasswordDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.cache.annotation.CacheEvict;
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
    private final PasswordEncoder passwordEncoder;
    private final CfService cfService;

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

    @Override
    @Transactional
    @CacheEvict(value = {"trainees", "allTrainees", "teamTrainees"}, allEntries = true)
    public TraineResponse updateProfile(UpdateProfileDto dto) {
        Long currentUserId = SecuiryUserUtil.getCurrntUserId();
        User user = getUserById(currentUserId);
        
        if (dto.userName() != null && !dto.userName().isBlank()) {
            user.setUsername(dto.userName());
        }
        
        if (dto.codeforcesHandle() != null && !dto.codeforcesHandle().isBlank() && !dto.codeforcesHandle().equals(user.getCodeforcesHandle())) {
            if (!cfService.checkIfUserCfAccountExist(dto.codeforcesHandle())) {
                throw new IllegalArgumentException("Codeforces account does not exist");
            }
            com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.CodeforcesUserInfo info = cfService.getUserInfo(dto.codeforcesHandle());
            user.setCodeforcesHandle(dto.codeforcesHandle());
            if (info != null) {
                if (info.rating() != null) user.setRate(info.rating());
                if (info.maxRate() != null && (user.getMaxRate() == null || info.maxRate() > user.getMaxRate())) user.setMaxRate(info.maxRate());
                if (info.rank() != null) user.setRank(info.rank());
                if (info.maxRank() != null) user.setMaxRank(info.maxRank());
            }
        }
        
        userRepository.save(user);
        return userMapper.toTraineResponse(user);
    }

    @Override
    @Transactional
    public void updatePassword(UpdatePasswordDto dto) {
        Long currentUserId = SecuiryUserUtil.getCurrntUserId();
        User user = getUserById(currentUserId);
        
        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }
        
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

}
