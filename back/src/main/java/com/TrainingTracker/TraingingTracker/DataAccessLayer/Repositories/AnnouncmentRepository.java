package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Announcment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncmentRepository  extends JpaRepository<Announcment,Long> {
}
