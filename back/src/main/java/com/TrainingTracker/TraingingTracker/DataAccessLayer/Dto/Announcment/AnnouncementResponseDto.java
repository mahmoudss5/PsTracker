package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment;

public record AnnouncementResponseDto(Long id,
                                      String type,
                                      String content,
                                      Long senderId,
                                      Long receiverId,
                                      java.time.LocalDateTime createdAt) {
}
