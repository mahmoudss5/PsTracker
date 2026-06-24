package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.TeamMessage;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamMessageService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage.TeamMessageResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.TeamMessage;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamMessageRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImpTeamMessageService implements TeamMessageService {

    private static final int MAX_MESSAGE_LENGTH = 256;

    private final TeamMessageRepository teamMessageRepository;
    private final TeamsService teamsService;
    private final UserService userService;
    private final TeamMessageMapper teamMessageMapper;
    private final SimpMessagingTemplate broker;

    @Override
    @Transactional
    public TeamMessageResponseDto sendMessage(Long teamId, Long senderId, TeamMessageCreateDto dto) {
        validateContent(dto);

        Team team = teamsService.getTeamById(teamId);
        User sender = userService.getUserById(senderId);
        requireTeamMember(team, senderId);

        TeamMessage message = TeamMessage.builder()
                .content(dto.content().trim())
                .createdAt(LocalDateTime.now())
                .team(team)
                .sender(sender)
                .build();

        TeamMessageResponseDto response = teamMessageMapper.toDto(teamMessageRepository.save(message));
        broker.convertAndSend("/topic/teams/" + teamId + "/messages", response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMessageResponseDto> getTeamMessages(Long teamId, Long requesterId) {
        Team team = teamsService.getTeamById(teamId);
        requireTeamMember(team, requesterId);

        return teamMessageRepository.findByTeamIdOrderByCreatedAtAscIdAsc(teamId)
                .stream()
                .map(teamMessageMapper::toDto)
                .toList();
    }

    private void validateContent(TeamMessageCreateDto dto) {
        if (dto == null || dto.content() == null || dto.content().isBlank()) {
            throw new AppException("Message content must not be blank", HttpStatus.BAD_REQUEST);
        }
        if (dto.content().trim().length() > MAX_MESSAGE_LENGTH) {
            throw new AppException("Message content must not exceed 256 characters", HttpStatus.BAD_REQUEST);
        }
    }

    private void requireTeamMember(Team team, Long userId) {
        boolean isCoach = team.getCoach() != null && Objects.equals(userId, team.getCoach().getId());
        boolean isTrainee = team.getTrainees().stream()
                .anyMatch(trainee -> Objects.equals(userId, trainee.getId()));

        if (!isCoach && !isTrainee) {
            throw new AppException("User is not a member of team " + team.getId(), HttpStatus.FORBIDDEN);
        }
    }
}
