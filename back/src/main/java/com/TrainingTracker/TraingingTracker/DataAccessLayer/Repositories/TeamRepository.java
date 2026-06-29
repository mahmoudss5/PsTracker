package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team,Long> {
  Team findByTeamName(String teamName);
  Optional<Team>findByTeamCode(String teamCode);
  Optional<Team>findById(Long id);
  Boolean existsByTeamCode(String teamCode);
  List<Team> findByCoachId(Long coachId);

  @Query("""
          select t.teamName
          from Team t
          join t.trainees trainee
          where trainee.id = :userId
          """)
  String findTeamNameByUserId(@Param("userId") Long userId);

}
