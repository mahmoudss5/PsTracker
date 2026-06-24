package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamMessageService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageResponseDto;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/team-messages")
@RequiredArgsConstructor
public class TeamMessageController {

    private final TeamMessageService teamMessageService;

    @Operation(summary = "Get a team's message history")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamMessageResponseDto>> getTeamMessages(
            @PathVariable Long teamId,
            Principal principal) {
        return ResponseEntity.ok(teamMessageService.getTeamMessages(teamId, getUserId(principal)));
    }

    @Operation(summary = "Send a message to a team")
    @PostMapping("/team/{teamId}")
    public ResponseEntity<TeamMessageResponseDto> sendMessage(
            @PathVariable Long teamId,
            @RequestBody TeamMessageCreateDto dto,
            Principal principal) {
        TeamMessageResponseDto response = teamMessageService.sendMessage(teamId, getUserId(principal), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @MessageMapping("/teams/{teamId}/messages")
    public void sendMessageOverWebSocket(
            @DestinationVariable Long teamId,
            @Payload TeamMessageCreateDto dto,
            Principal principal) {
        teamMessageService.sendMessage(teamId, getUserId(principal), dto);
    }

    private Long getUserId(Principal principal) {
        if (principal instanceof Authentication authentication
                && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            return securityUser.getId();
        }
        throw new AppException("Authentication required", HttpStatus.UNAUTHORIZED);
    }
}
