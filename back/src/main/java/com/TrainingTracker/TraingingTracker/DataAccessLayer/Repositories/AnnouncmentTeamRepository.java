package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncmentTeamRepository  extends JpaRepository<AnnouncmentTeam,AnnouncmentTeam> {
  List<AnnouncmentTeam> findByTeamId(Long teamId);
}
