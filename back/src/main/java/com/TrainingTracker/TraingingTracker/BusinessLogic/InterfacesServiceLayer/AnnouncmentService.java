package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncementResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncmentCreateDto;

import java.util.List;

public interface AnnouncmentService {

    void sendAnnouncment(AnnouncmentCreateDto announcmentCreateDto);
    List<AnnouncementResponseDto> getAllAnnouncmentsForUser(Long userId);
    List<AnnouncementResponseDto> getAllAnnouncmentsForTeam(Long teamId);

}
