package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment;


public record AnnouncmentCreateDto(
        String type,
        String content,
        Long senderId,
        boolean isTeamAnnouncment,
        Long receiverId) {
 }


