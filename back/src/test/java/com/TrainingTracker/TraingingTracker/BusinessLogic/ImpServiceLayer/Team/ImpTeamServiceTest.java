package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Team;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpTeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ImpTeamService teamService;

    @Test
    void testCreateTeam() {
        Long coachId = 1L;
        User coach = User.builder().id(coachId).username("coach").build();

        try (MockedStatic<SecuiryUserUtil> securityUtilMockedStatic = mockStatic(SecuiryUserUtil.class)) {
            securityUtilMockedStatic.when(SecuiryUserUtil::getCurrntUserId).thenReturn(coachId);
            when(userService.getUserById(coachId)).thenReturn(coach);
            when(teamRepository.existsByTeamCode(anyString())).thenReturn(false);

            String teamCode = teamService.createTeam("Alpha Team");

            assertNotNull(teamCode);
            assertEquals(6, teamCode.length());
            verify(userService).getUserById(coachId);
            verify(teamRepository).existsByTeamCode(teamCode);
        }
    }

    @Test
    void testJoinTeam_Success() {
        String teamCode = "ABC123";
        Team team = Team.builder()
                .id(100L)
                .teamCode(teamCode)
                .teamName("Alpha")
                .trainees(new ArrayList<>())
                .build();
        Long traineeId = 2L;
        User trainee = User.builder().id(traineeId).username("trainee").build();

        try (MockedStatic<SecuiryUserUtil> securityUtilMockedStatic = mockStatic(SecuiryUserUtil.class)) {
            securityUtilMockedStatic.when(SecuiryUserUtil::getCurrntUserId).thenReturn(traineeId);
            when(teamRepository.findByTeamCode(teamCode)).thenReturn(Optional.of(team));
            when(userService.getUserById(traineeId)).thenReturn(trainee);

            teamService.JoinTeam(teamCode);

            assertTrue(team.getTrainees().contains(trainee));
            verify(teamRepository).save(team);
        }
    }

    @Test
    void testJoinTeam_TeamFull() {
        String teamCode = "ABC123";
        List<User> traineesList = new ArrayList<>();
        traineesList.add(User.builder().id(10L).build());
        traineesList.add(User.builder().id(11L).build());
        traineesList.add(User.builder().id(12L).build());
        traineesList.add(User.builder().id(13L).build());

        Team team = Team.builder()
                .id(100L)
                .teamCode(teamCode)
                .teamName("Alpha")
                .trainees(traineesList)
                .build();

        when(teamRepository.findByTeamCode(teamCode)).thenReturn(Optional.of(team));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> teamService.JoinTeam(teamCode));
        assertEquals("Team is full", exception.getMessage());
        verify(teamRepository, never()).save(any());
    }

    @Test
    void testLeaveTeam_Success() {
        Long teamId = 100L;
        Long userId = 2L;
        User user = User.builder().id(userId).build();
        List<User> traineesList = new ArrayList<>();
        traineesList.add(user);

        Team team = Team.builder()
                .id(teamId)
                .teamName("Alpha")
                .trainees(traineesList)
                .build();

        try (MockedStatic<SecuiryUserUtil> securityUtilMockedStatic = mockStatic(SecuiryUserUtil.class)) {
            securityUtilMockedStatic.when(SecuiryUserUtil::getCurrntUserId).thenReturn(userId);
            when(userService.getUserById(userId)).thenReturn(user);
            when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

            teamService.leaveTeam(teamId);

            assertFalse(team.getTrainees().contains(user));
            verify(teamRepository).save(team);
        }
    }

    @Test
    void testGetTeamById_Success() {
        Long teamId = 100L;
        Team team = Team.builder().id(teamId).teamName("Alpha").build();
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        Team result = teamService.getTeamById(teamId);

        assertNotNull(result);
        assertEquals("Alpha", result.getTeamName());
    }

    @Test
    void testGetTeamById_NotFound() {
        Long teamId = 999L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> teamService.getTeamById(teamId));
        assertEquals("Team not found with id: " + teamId, exception.getMessage());
    }
}
