package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Announcment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Announcment;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncementResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncmentCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.AnnouncmentType;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey.AnnouncmnentTeamId;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey.AnnouncmentUserId;

import java.time.LocalDateTime;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentTeam;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentTeamRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentUserRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentUser;


@Component
@RequiredArgsConstructor
public class AnnouncmentMapper {

    private final UserService userService;
    private final TeamsService teamsService;
    private final AnnouncmentRepository announcmentRepository;
    private final AnnouncmentTeamRepository announcmentTeamRepository;
    private final AnnouncmentUserRepository announcmentUserRepository;

    private Announcment handleTeamAnnouncment(AnnouncmentCreateDto announcmentCreateDto, User sender) {
        Team team = teamsService.getTeamById(announcmentCreateDto.receiverId());
        Announcment announcment = Announcment.builder()
                .sender(sender)
                .content(announcmentCreateDto.content())
                .type(AnnouncmentType.valueOf(announcmentCreateDto.type()))
                .createdAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();

        announcmentRepository.save(announcment);

        // Explicitly build and set the composite key so Hibernate does not have
        // to resolve it via @MapsId reflection (which fails in Hibernate 7).
        AnnouncmnentTeamId compositeKey = new AnnouncmnentTeamId(
                announcment.getId(),
                team.getId()
        );

        AnnouncmentTeam announcmentTeam = AnnouncmentTeam.builder()
                .announcmnetTeamId(compositeKey)
                .announcment(announcment)
                .team(team)
                .build();
        announcmentTeamRepository.save(announcmentTeam);
        return announcment;
    }


    private Announcment handleUserAnnouncment(AnnouncmentCreateDto announcmentCreateDto, User sender) {
        User user = userService.getUserById(announcmentCreateDto.receiverId());
        if (user == null) throw new RuntimeException("User not found");
        Announcment announcment = Announcment.builder()
                .sender(sender)
                .content(announcmentCreateDto.content())
                .type(AnnouncmentType.valueOf(announcmentCreateDto.type()))
                .createdAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();
        announcmentRepository.save(announcment);

        // Explicitly build and set the composite key.
        AnnouncmentUserId compositeKey = new AnnouncmentUserId(
                announcment.getId(),
                user.getId()
        );

        AnnouncmentUser announcmentUser = AnnouncmentUser.builder()
                .announcmentUserId(compositeKey)
                .announcment(announcment)
                .user(user)
                .build();
        announcmentUserRepository.save(announcmentUser);
        return announcment;
    }

    public Announcment toEntityWithSave(AnnouncmentCreateDto announcmentCreateDto) {
        User sender = userService.getUserById(announcmentCreateDto.senderId());
        if (sender == null) throw new RuntimeException("Sender not found");

        Announcment announcment = new Announcment();
        if (announcmentCreateDto.isTeamAnnouncment()) {

            announcment = handleTeamAnnouncment(announcmentCreateDto, sender);
        } else {
            announcment = handleUserAnnouncment(announcmentCreateDto, sender);
        }
        return announcment;
    }


    public AnnouncementResponseDto toAnnouncmentResponseDto(Announcment announcment, Long revicerId) {
        AnnouncementResponseDto dto = new AnnouncementResponseDto(
                announcment.getId(),
                announcment.getType().name(),
                announcment.getContent(),
                announcment.getSender().getId(),
                revicerId,
                announcment.getCreatedAt()
        );
          return dto;
    }


}
