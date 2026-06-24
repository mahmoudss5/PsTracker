package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamJoinDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock
    private TeamsService teamsService;

    @InjectMocks
    private TeamController teamController;

    @Test
    void testCreateTeam() {
        TeamCreateDto dto = new TeamCreateDto("Alpha Team");
        when(teamsService.createTeam("Alpha Team")).thenReturn("ABC123");

        ResponseEntity<Map<String, String>> response = teamController.createTeam(dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ABC123", response.getBody().get("teamCode"));
        verify(teamsService).createTeam("Alpha Team");
    }

    @Test
    void testJoinTeam() {
        TeamJoinDto dto = new TeamJoinDto("ABC123");
        doNothing().when(teamsService).JoinTeam("ABC123");

        ResponseEntity<Void> response = teamController.joinTeam(dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamsService).JoinTeam("ABC123");
    }

    @Test
    void testLeaveTeam() {
        Long teamId = 100L;
        doNothing().when(teamsService).leaveTeam(teamId);

        ResponseEntity<Void> response = teamController.leaveTeam(teamId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamsService).leaveTeam(teamId);
    }

    @Test
    void testGetTeamById() {
        Long teamId = 100L;
        TeamResponseDto responseDto = new TeamResponseDto(teamId, "Alpha Team", "ABC123", 1L, "coach", List.of());
        when(teamsService.getTeamResponseById(teamId)).thenReturn(responseDto);

        ResponseEntity<TeamResponseDto> response = teamController.getTeamById(teamId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(teamId, response.getBody().id());
        assertEquals("Alpha Team", response.getBody().teamName());
        assertEquals("ABC123", response.getBody().teamCode());
        verify(teamsService).getTeamResponseById(teamId);
    }
}
