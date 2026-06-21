package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Announcment;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.AnnouncmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncmentRepository extends JpaRepository<Announcment, Long> {
    List<Announcment> findBySenderId(Long senderId);

    List<Announcment> findByType(AnnouncmentType type);

    List<Announcment> findByStatus(String status);

    List<Announcment> findBySenderIdAndStatus(Long senderId, String status);
}
