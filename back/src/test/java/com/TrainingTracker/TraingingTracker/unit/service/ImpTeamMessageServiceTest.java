package com.TrainingTracker.TraingingTracker.unit.service;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.TeamMessage.ImpTeamMessageService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.TeamMessage.TeamMessageMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.TeamMessage;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamMessageRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpTeamMessageServiceTest {

    @Mock
    private TeamMessageRepository teamMessageRepository;

    @Mock
    private TeamsService teamsService;

    @Mock
    private UserService userService;

    @Mock
    private SimpMessagingTemplate broker;

    private ImpTeamMessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new ImpTeamMessageService(
                teamMessageRepository, teamsService, userService, new TeamMessageMapper(), broker);
    }

    @Test
    void coachCanSendTrimmedMessageAndSubscribersAreNotified() {
        User coach = User.builder().id(4L).username("coach").build();
        Team team = Team.builder().id(3L).coach(coach).build();
        when(teamsService.getTeamById(3L)).thenReturn(team);
        when(userService.getUserById(4L)).thenReturn(coach);
        when(teamMessageRepository.save(any(TeamMessage.class))).thenAnswer(invocation -> {
            TeamMessage message = invocation.getArgument(0);
            message.setId(10L);
            return message;
        });

        TeamMessageResponseDto result = messageService.sendMessage(3L, 4L, new TeamMessageCreateDto("  Hello  "));

        ArgumentCaptor<TeamMessage> saved = ArgumentCaptor.forClass(TeamMessage.class);
        InOrder order = inOrder(teamMessageRepository, broker);
        order.verify(teamMessageRepository).save(saved.capture());
        order.verify(broker).convertAndSend("/topic/teams/3/messages", result);
        assertEquals("Hello", saved.getValue().getContent());
        assertEquals(3L, saved.getValue().getTeam().getId());
        assertEquals(4L, saved.getValue().getSender().getId());
        assertEquals(10L, result.id());
        assertEquals("coach", result.senderUsername());
        assertNotNull(result.createdAt());
    }

    @Test
    void traineeCanSendMessageToOwnTeam() {
        User trainee = User.builder().id(5L).username("trainee").build();
        Team team = Team.builder().id(3L).trainees(List.of(trainee)).build();
        when(teamsService.getTeamById(3L)).thenReturn(team);
        when(userService.getUserById(5L)).thenReturn(trainee);
        when(teamMessageRepository.save(any(TeamMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TeamMessageResponseDto result = messageService.sendMessage(3L, 5L, new TeamMessageCreateDto("Hi"));

        assertEquals(5L, result.senderId());
        verify(broker).convertAndSend("/topic/teams/3/messages", result);
    }

    @Test
    void rejectsNullOrBlankContentBeforeLookingUpTeam() {
        List<TeamMessageCreateDto> invalid = java.util.Arrays.asList(
                null, new TeamMessageCreateDto(null), new TeamMessageCreateDto("  "));

        for (TeamMessageCreateDto dto : invalid) {
            AppException error = assertThrows(AppException.class, () -> messageService.sendMessage(3L, 5L, dto));
            assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        }

        verify(teamsService, never()).getTeamById(any());
        verify(teamMessageRepository, never()).save(any());
    }

    @Test
    void rejectsContentLongerThan256CharactersAfterTrimming() {
        AppException error = assertThrows(AppException.class,
                () -> messageService.sendMessage(3L, 5L, new TeamMessageCreateDto(" " + "x".repeat(257) + " ")));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        verify(teamMessageRepository, never()).save(any());
    }

    @Test
    void rejectsMessageFromNonMemberWithoutSavingOrBroadcasting() {
        Team team = Team.builder().id(3L).coach(User.builder().id(4L).build()).build();
        when(teamsService.getTeamById(3L)).thenReturn(team);
        when(userService.getUserById(5L)).thenReturn(User.builder().id(5L).build());

        AppException error = assertThrows(AppException.class,
                () -> messageService.sendMessage(3L, 5L, new TeamMessageCreateDto("Hello")));

        assertEquals(HttpStatus.FORBIDDEN, error.getStatus());
        verify(teamMessageRepository, never()).save(any());
        verify(broker, never()).convertAndSend(any(String.class), any(Object.class));
    }

    @Test
    void memberCanReadMessagesInRepositoryOrder() {
        User trainee = User.builder().id(5L).username("trainee").build();
        Team team = Team.builder().id(3L).trainees(List.of(trainee)).build();
        TeamMessage first = TeamMessage.builder().id(1L).content("First")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).team(team).sender(trainee).build();
        TeamMessage second = TeamMessage.builder().id(2L).content("Second")
                .createdAt(LocalDateTime.of(2026, 1, 1, 11, 0)).team(team).sender(trainee).build();
        when(teamsService.getTeamById(3L)).thenReturn(team);
        when(teamMessageRepository.findByTeamIdOrderByCreatedAtAscIdAsc(3L)).thenReturn(List.of(first, second));

        List<TeamMessageResponseDto> result = messageService.getTeamMessages(3L, 5L);

        assertEquals(List.of("First", "Second"), result.stream().map(TeamMessageResponseDto::content).toList());
        assertEquals(5L, result.getFirst().senderId());
    }

    @Test
    void rejectsHistoryRequestFromNonMember() {
        Team team = Team.builder().id(3L).coach(User.builder().id(4L).build()).build();
        when(teamsService.getTeamById(3L)).thenReturn(team);

        AppException error = assertThrows(AppException.class, () -> messageService.getTeamMessages(3L, 5L));

        assertEquals(HttpStatus.FORBIDDEN, error.getStatus());
        verify(teamMessageRepository, never()).findByTeamIdOrderByCreatedAtAscIdAsc(any());
    }
}
