package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpUserService implements UserService {

    private final UserRepository userRepository;


    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));
    }
}
