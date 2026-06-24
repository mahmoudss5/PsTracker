package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamJoinDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamsService teamsService;

    @Operation(summary = "Create a team")
    @PostMapping
    public ResponseEntity<Map<String, String>> createTeam(@RequestBody TeamCreateDto dto) {
        String teamCode = teamsService.createTeam(dto.teamName());
        return ResponseEntity.ok(Map.of("teamCode", teamCode));
    }

    @Operation(summary = "Join a team using its code")
    @PostMapping("/join")
    public ResponseEntity<Void> joinTeam(@RequestBody TeamJoinDto dto) {
        teamsService.JoinTeam(dto.teamCode());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Leave a team")
    @PostMapping("/leave/{teamId}")
    public ResponseEntity<Void> leaveTeam(@PathVariable Long teamId) {
        teamsService.leaveTeam(teamId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get team details by id")
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponseDto> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamsService.getTeamResponseById(id));
    }
}
