package com.TrainingTracker.TraingingTracker.unit.Controllers.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamMessageService;
import com.TrainingTracker.TraingingTracker.Controllers.TeamMessageController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMessageControllerTest {

    @Mock
    private TeamMessageService teamMessageService;

    @InjectMocks
    private TeamMessageController teamMessageController;

    private Principal principalFor(Long userId) {
        User user = User.builder().id(userId).build();
        return new UsernamePasswordAuthenticationToken(new SecurityUser(user), null, List.of());
    }

    private TeamMessageResponseDto responseDto() {
        return new TeamMessageResponseDto(1L, "Ready to practice", OffsetDateTime.parse("2025-01-10T12:00:00Z"),
                20L, 10L, "John");
    }

    @Test
    void testGetTeamMessages() {
        Long teamId = 20L;
        Long userId = 10L;
        Principal principal = principalFor(userId);
        List<TeamMessageResponseDto> dtoList = List.of(responseDto());
        when(teamMessageService.getTeamMessages(teamId, userId)).thenReturn(dtoList);

        ResponseEntity<List<TeamMessageResponseDto>> response = teamMessageController.getTeamMessages(teamId, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(teamMessageService).getTeamMessages(teamId, userId);
    }

    @Test
    void testSendMessage() {
        Long teamId = 20L;
        Long userId = 10L;
        Principal principal = principalFor(userId);
        TeamMessageCreateDto request = new TeamMessageCreateDto("Ready to practice");
        TeamMessageResponseDto dto = responseDto();
        when(teamMessageService.sendMessage(teamId, userId, request)).thenReturn(dto);

        ResponseEntity<TeamMessageResponseDto> response = teamMessageController.sendMessage(teamId, request, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(teamMessageService).sendMessage(teamId, userId, request);
    }

    @Test
    void testSendMessageOverWebSocket() {
        Long teamId = 20L;
        Long userId = 10L;
        Principal principal = principalFor(userId);
        TeamMessageCreateDto request = new TeamMessageCreateDto("Ready to practice");

        teamMessageController.sendMessageOverWebSocket(teamId, request, principal);

        verify(teamMessageService).sendMessage(teamId, userId, request);
    }

    @Test
    void testSendMessageOverWebSocketWithoutAuthenticationShouldFail() {
        Long teamId = 20L;
        TeamMessageCreateDto request = new TeamMessageCreateDto("Ready to practice");

        AppException error = assertThrows(AppException.class,
                () -> teamMessageController.sendMessageOverWebSocket(teamId, request, null));

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatus());
        verifyNoInteractions(teamMessageService);
    }
}
