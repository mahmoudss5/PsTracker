package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.AuthResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignInDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignUpDto;

public interface AuthService {
    AuthResponse signIn(SignInDto signDto);
    AuthResponse signUp(SignUpDto dto);
    void logout();
    String refreshToken(String RefreshToken);
    String getNewRefreshToken(String RefreshToken);
}
