package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.TeamMessage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMessageRepository extends JpaRepository<TeamMessage,Long> {
 List<TeamMessage> findByTeamId(Long teamId);

 @EntityGraph(attributePaths = {"sender", "team"})
 List<TeamMessage> findByTeamIdOrderByCreatedAtAscIdAsc(Long teamId);
}
