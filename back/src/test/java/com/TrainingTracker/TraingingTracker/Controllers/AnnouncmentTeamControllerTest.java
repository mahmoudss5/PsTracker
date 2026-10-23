package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.AnnouncmentService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncementResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncmentCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncmentTeamControllerTest {

    @Mock
    private AnnouncmentService announcmentService;

    @InjectMocks
    private AnnouncmentTeamController announcmentTeamController;

    @Test
    void testGetAllForTeam() {
        Long teamId = 1L;
        AnnouncementResponseDto announcement = new AnnouncementResponseDto(
                10L,
                "INFO",
                "Team Announcement content",
                5L,
                1L,
                LocalDateTime.now()
        );
        when(announcmentService.getAllAnnouncmentsForTeam(teamId)).thenReturn(List.of(announcement));

        ResponseEntity<List<AnnouncementResponseDto>> response = announcmentTeamController.getAllForTeam(teamId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Team Announcement content", response.getBody().get(0).content());
        verify(announcmentService).getAllAnnouncmentsForTeam(teamId);
    }

    @Test
    void testSendAnnouncementToTeam() {
        Long authenticatedUserId = 42L;
        AnnouncmentCreateDto inputDto = new AnnouncmentCreateDto(
                "INFO",
                "New team update",
                5L,
                false, // Input has false, but controller should override to true
                1L
        );
        doNothing().when(announcmentService).sendAnnouncment(any(AnnouncmentCreateDto.class));

        User authenticatedUser = User.builder()
                .id(authenticatedUserId)
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new SecurityUser(authenticatedUser),
                        null,
                        List.of()
                )
        );

        ResponseEntity<Void> response;
        try {
            response = announcmentTeamController.sendAnnouncementToTeam(inputDto);
        } finally {
            SecurityContextHolder.clearContext();
        }

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ArgumentCaptor<AnnouncmentCreateDto> captor = ArgumentCaptor.forClass(AnnouncmentCreateDto.class);
        verify(announcmentService).sendAnnouncment(captor.capture());

        AnnouncmentCreateDto capturedDto = captor.getValue();
        assertEquals("INFO", capturedDto.type());
        assertEquals("New team update", capturedDto.content());
        assertEquals(authenticatedUserId, capturedDto.senderId());
        assertTrue(capturedDto.isTeamAnnouncment()); // Must be overridden to true
        assertEquals(1L, capturedDto.receiverId());
    }
}
