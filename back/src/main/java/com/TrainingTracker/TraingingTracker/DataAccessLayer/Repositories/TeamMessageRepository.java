package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.TeamMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMessageRepository extends JpaRepository<TeamMessage,Long> {
}
