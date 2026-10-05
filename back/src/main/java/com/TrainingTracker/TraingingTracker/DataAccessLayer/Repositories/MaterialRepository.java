package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByTeamIdOrderByCreatedAtDesc(Long teamId);
    Optional<Material> findByS3KeyAndTeamId(String s3Key, Long teamId);
    Optional<Material> findByS3Key(String s3Key);
}
