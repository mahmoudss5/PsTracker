package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;


import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentUser;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey.AnnouncmentUserId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
    

public interface AnnouncmentUserRepository extends JpaRepository<AnnouncmentUser, AnnouncmentUserId> {
  List<AnnouncmentUser> findByUserId(Long userId);  

}
