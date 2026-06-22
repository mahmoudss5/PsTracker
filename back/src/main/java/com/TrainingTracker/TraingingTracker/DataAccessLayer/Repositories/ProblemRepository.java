package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Optional<Problem> findByName(String name);
}
