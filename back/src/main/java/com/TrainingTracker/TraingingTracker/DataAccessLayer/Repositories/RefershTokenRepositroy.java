package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefershTokenRepositroy extends JpaRepository<RefreshToken,Long> {
  Optional<RefreshToken> findByHasedRefreshToken(String token);
  void deleteByUserId(Long userId);

}
