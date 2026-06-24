package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Announcment;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncementResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncmentCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Announcment;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentTeam;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentUser;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentTeamRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpAnnouncmentServiceTest {

    @Mock
    private AnnouncmentMapper announcmentMapper;

    @Mock
    private AnnouncmentRepository announcmentRepository;

    @Mock
    private AnnouncmentUserRepository announcmentUserRepository;

    @Mock
    private AnnouncmentTeamRepository announcmentTeamRepository;

    @Mock
    private SimpMessagingTemplate broker;

    @Mock
    private UserService userService;

    @Mock
    private TeamsService teamsService;

    @InjectMocks
    private ImpAnnouncmentService announcmentService;

    @Test
    void testSendAnnouncment_Team() {
        AnnouncmentCreateDto createDto = new AnnouncmentCreateDto(
                "INFO", "Maintenance tomorrow", 1L, true, 100L
        );
        Announcment announcment = Announcment.builder().id(10L).content("Maintenance tomorrow").build();
        Team team = Team.builder().id(100L).teamName("Alpha").build();

        when(announcmentMapper.toEntityWithSave(createDto)).thenReturn(announcment);
        when(teamsService.getTeamById(100L)).thenReturn(team);

        announcmentService.sendAnnouncment(createDto);

        verify(announcmentMapper).toEntityWithSave(createDto);
        verify(teamsService).getTeamById(100L);
        verify(broker).convertAndSend("/topic/teams/100/announcments", announcment);
        verifyNoInteractions(userService);
    }

    @Test
    void testSendAnnouncment_User() {
        AnnouncmentCreateDto createDto = new AnnouncmentCreateDto(
                "WARNING", "Alert content", 1L, false, 200L
        );
        Announcment announcment = Announcment.builder().id(11L).content("Alert content").build();
        User user = User.builder().id(200L).email("user@example.com").build();

        when(announcmentMapper.toEntityWithSave(createDto)).thenReturn(announcment);
        when(userService.getUserById(200L)).thenReturn(user);

        announcmentService.sendAnnouncment(createDto);

        verify(announcmentMapper).toEntityWithSave(createDto);
        verify(userService).getUserById(200L);
        verify(broker).convertAndSendToUser("user@example.com", "/queue/notifications", announcment);
        verifyNoInteractions(teamsService);
    }

    @Test
    void testGetAllAnnouncmentsForUser() {
        Long userId = 1L;
        Announcment ann = Announcment.builder().id(10L).content("Msg").build();
        AnnouncmentUser annUser = AnnouncmentUser.builder().announcment(ann).build();
        AnnouncementResponseDto responseDto = new AnnouncementResponseDto(10L, "INFO", "Msg", 2L, userId, LocalDateTime.now());

        when(announcmentUserRepository.findByUserId(userId)).thenReturn(List.of(annUser));
        when(announcmentMapper.toUserDto(ann, userId)).thenReturn(responseDto);

        List<AnnouncementResponseDto> results = announcmentService.getAllAnnouncmentsForUser(userId);

        assertEquals(1, results.size());
        assertEquals("Msg", results.get(0).content());
        verify(announcmentUserRepository).findByUserId(userId);
        verify(announcmentMapper).toUserDto(ann, userId);
    }

    @Test
    void testGetAllAnnouncmentsForTeam() {
        Long teamId = 100L;
        Announcment ann = Announcment.builder().id(10L).content("Msg Team").build();
        AnnouncmentTeam annTeam = AnnouncmentTeam.builder().announcment(ann).build();
        AnnouncementResponseDto responseDto = new AnnouncementResponseDto(10L, "INFO", "Msg Team", 2L, teamId, LocalDateTime.now());

        when(announcmentTeamRepository.findByTeamId(teamId)).thenReturn(List.of(annTeam));
        when(announcmentMapper.toTeamDto(ann, teamId)).thenReturn(responseDto);

        List<AnnouncementResponseDto> results = announcmentService.getAllAnnouncmentsForTeam(teamId);

        assertEquals(1, results.size());
        assertEquals("Msg Team", results.get(0).content());
        verify(announcmentTeamRepository).findByTeamId(teamId);
        verify(announcmentMapper).toTeamDto(ann, teamId);
    }
}
