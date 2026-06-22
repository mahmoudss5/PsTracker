package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {
    @Query("""
            select count(distinct s.problem.id)
            from Submission s
            where s.user.id = :userId
              and upper(s.verdict) in ('OK', 'ACCEPTED')
            """)
    Long countSolvedProblemsByUserId(@Param("userId") Long userId);

    List<Submission> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Submission> findByProblemIdOrderByCreatedAtDesc(Long problemId);
    
    
    
    
  @Query("""
        select s
        from Submission s
        where s.user.id = :userId
        and upper(s.verdict) in ('OK', 'ACCEPTED')
        and s.createdAt >= :startOfDay
        and s.createdAt < :startOfNextDay
        """)
List<Submission> findAcceptedSubmissionsByUserAndDateRange(
        @Param("userId") Long userId,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("startOfNextDay") LocalDateTime startOfNextDay
);

}
