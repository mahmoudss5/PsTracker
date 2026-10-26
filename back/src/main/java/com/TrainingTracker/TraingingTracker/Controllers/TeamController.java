package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamJoinDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team.TeamResponseDto;
import com.TrainingTracker.TraingingTracker.Security.Aop.isSame.Team.IsTeamMember;
import com.TrainingTracker.TraingingTracker.Security.Aop.RateLimit.RateLimit;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamsService teamsService;

    @Operation(summary = "Create a team")
    @PostMapping
    @PreAuthorize("hasAuthority('Coach')")
    public ResponseEntity<Map<String, String>> createTeam(@Valid @RequestBody TeamCreateDto dto) {
        String teamCode = teamsService.createTeam(dto.teamName());
        return ResponseEntity.ok(Map.of("teamCode", teamCode));
    }

    @Operation(summary = "Join a team using its code")
    @PostMapping("/join")
    @RateLimit(requests = 10, windowSeconds = 60, key = "team:join")
    public ResponseEntity<Void> joinTeam(@RequestBody TeamJoinDto dto) {
        teamsService.JoinTeam(dto.teamCode());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Leave a team")
    @PostMapping("/leave/{teamId}")
    @IsTeamMember
    public ResponseEntity<Void> leaveTeam(@PathVariable Long teamId) {
        teamsService.leaveTeam(teamId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get team details by id")
    @GetMapping("/{id}")
    @IsTeamMember
    public ResponseEntity<TeamResponseDto> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamsService.getTeamResponseById(id));
    }

    @Operation(summary = "Get teams created by the current coach")
    @GetMapping("/coach/me")
    public ResponseEntity<List<TeamResponseDto>> getCurrentCoachTeams() {
        return ResponseEntity.ok(teamsService.getCurrentCoachTeams());
    }
}
