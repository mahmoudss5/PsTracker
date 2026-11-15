package com.TrainingTracker.TraingingTracker.unit.Controllers.Controllers;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.Controllers.TeamController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamJoinDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamResponseDto;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.TeamError;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock
    private TeamsService teamsService;

    @InjectMocks
    private TeamController teamController;

    @Test
    void testCreateTeamShouldCreateTeam() {
        TeamCreateDto dto = new TeamCreateDto("Alpha Team");
        when(teamsService.createTeam("Alpha Team")).thenReturn("ABC123");

        ResponseEntity<Map<String, String>> response = teamController.createTeam(dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ABC123", response.getBody().get("teamCode"));
        verify(teamsService).createTeam("Alpha Team");
    }

    @Test
    void testCreateTeamWithDuplicatedTeamNameShouldFail() {
        TeamCreateDto dto = new TeamCreateDto("Alpha Team");
        when(teamsService.createTeam("Alpha Team")).thenThrow(new TeamError("Team name already exists"));

        TeamError error = assertThrows(TeamError.class, () -> teamController.createTeam(dto));

        assertEquals("Team name already exists", error.getMessage());
        verify(teamsService).createTeam("Alpha Team");
    }

    @Test
    void testJoinTeamShouldJoinTeam() {
        TeamJoinDto dto = new TeamJoinDto("ABC123");
        doNothing().when(teamsService).JoinTeam("ABC123");

        ResponseEntity<Void> response = teamController.joinTeam(dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamsService).JoinTeam("ABC123");
    }

    @Test
    void testJoinTeamWithInvalidCodeShouldFail() {
        TeamJoinDto dto = new TeamJoinDto("INVALID");
        doThrow(new TeamError("Team not found with code: INVALID")).when(teamsService).JoinTeam("INVALID");

        TeamError error = assertThrows(TeamError.class, () -> teamController.joinTeam(dto));

        assertEquals("Team not found with code: INVALID", error.getMessage());
        verify(teamsService).JoinTeam("INVALID");
    }

    @Test
    void testLeaveTeamShouldLeaveTeam() {
        Long teamId = 100L;
        doNothing().when(teamsService).leaveTeam(teamId);

        ResponseEntity<Void> response = teamController.leaveTeam(teamId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamsService).leaveTeam(teamId);
    }

    @Test
    void shouldFailToLeaveTeamWithInvalidId() {
        Long teamId = 100L;
        String message = "Team not found with id: " + teamId;
        doThrow(new RuntimeException(message)).when(teamsService).leaveTeam(teamId);

        RuntimeException error = assertThrows(RuntimeException.class, () -> teamController.leaveTeam(teamId));

        assertEquals(message, error.getMessage());
        verify(teamsService).leaveTeam(teamId);
    }

    @Test
    void shouldFailToLeaveTeamForCoach() {
        Long teamId = 10L;
        String message = "Coach can not leave the Team he create";
        doThrow(new TeamError(message)).when(teamsService).leaveTeam(teamId);

        TeamError error = assertThrows(TeamError.class, () -> teamController.leaveTeam(teamId));

        assertEquals(message, error.getMessage());
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

    @Test
    void testGetTeamByIdNotFound() {
        Long teamId = 100L;
        String message = "Team not found with id: " + teamId;
        when(teamsService.getTeamResponseById(teamId)).thenThrow(new RuntimeException(message));

        RuntimeException error = assertThrows(RuntimeException.class, () -> teamController.getTeamById(teamId));

        assertEquals(message, error.getMessage());
        verify(teamsService).getTeamResponseById(teamId);
    }

    @Test
    void testGetCurrentCoachTeams() {
        TeamResponseDto team = new TeamResponseDto(100L, "Alpha Team", "ABC123", 1L, "coach", List.of());
        List<TeamResponseDto> teams = List.of(team);
        when(teamsService.getCurrentCoachTeams()).thenReturn(teams);

        ResponseEntity<List<TeamResponseDto>> response = teamController.getCurrentCoachTeams();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(teams, response.getBody());
        verify(teamsService).getCurrentCoachTeams();
    }

    @Test
    void testGetCurrentCoachTeamsWhenCoachHasNoTeams() {
        when(teamsService.getCurrentCoachTeams()).thenReturn(List.of());

        ResponseEntity<List<TeamResponseDto>> response = teamController.getCurrentCoachTeams();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
        verify(teamsService).getCurrentCoachTeams();
    }
}
