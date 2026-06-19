package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team,Long> {
  Team findByTeamName(String teamName);
  Optional<Team>findByTeamCode(String teamCode);
  Boolean existsByTeamCode(String teamCode);
}
