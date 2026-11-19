package com.TrainingTracker.TraingingTracker.unit.controller;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.AnnouncmentService;
import com.TrainingTracker.TraingingTracker.Controllers.AnnouncmentController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncementResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncmentCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncmentControllerTest {

    @Mock
    private AnnouncmentService announcmentService;

    @InjectMocks
    private AnnouncmentController announcmentController;

    private void authenticate(Long userId) {
        User user = User.builder().id(userId).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new SecurityUser(user), null, List.of()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private AnnouncementResponseDto responseDto(Long receiverId) {
        return new AnnouncementResponseDto(1L, "INFO", "Practice update", 42L, receiverId,
                LocalDateTime.of(2025, 1, 10, 12, 0));
    }

    @Test
    void testGetAllUsesAuthenticatedUser() {
        Long userId = 10L;
        authenticate(userId);
        List<AnnouncementResponseDto> dtoList = List.of(responseDto(userId));
        when(announcmentService.getAllAnnouncmentsForUser(userId)).thenReturn(dtoList);

        ResponseEntity<List<AnnouncementResponseDto>> response = announcmentController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(announcmentService).getAllAnnouncmentsForUser(userId);
    }

    @Test
    void testGetAllForTeam() {
        Long teamId = 20L;
        List<AnnouncementResponseDto> dtoList = List.of(responseDto(teamId));
        when(announcmentService.getAllAnnouncmentsForTeam(teamId)).thenReturn(dtoList);

        ResponseEntity<List<AnnouncementResponseDto>> response = announcmentController.geAllForTeam(teamId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(announcmentService).getAllAnnouncmentsForTeam(teamId);
    }

    @Test
    void testSendAnnouncmentUsesAuthenticatedSender() {
        Long senderId = 42L;
        authenticate(senderId);
        AnnouncmentCreateDto request = new AnnouncmentCreateDto("INFO", "Practice update", 99L, true, 20L);
        AnnouncmentCreateDto expected = new AnnouncmentCreateDto("INFO", "Practice update", senderId, true, 20L);

        announcmentController.sendAnnouncment(request);

        verify(announcmentService).sendAnnouncment(expected);
    }
}
