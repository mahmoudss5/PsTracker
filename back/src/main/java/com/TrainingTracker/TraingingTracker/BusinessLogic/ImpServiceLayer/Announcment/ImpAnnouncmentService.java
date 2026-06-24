package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Announcment;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.AnnouncmentService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncementResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncmentCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Announcment;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentTeam;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentUser;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentTeamRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpAnnouncmentService implements AnnouncmentService {

    private static final String READ_STATUS = "READ";

    private final AnnouncmentMapper announcmentMapper;
    private final AnnouncmentRepository announcmentRepository;
    private final AnnouncmentUserRepository announcmentUserRepository;
    private final AnnouncmentTeamRepository announcmentTeamRepository;
    private final SimpMessagingTemplate broker;
    private final UserService userService;
    private final TeamsService teamsService;


    @Override
    @Transactional
    @CacheEvict(value = {"userAnnouncements", "teamAnnouncements"}, allEntries = true)
    public void sendAnnouncment(AnnouncmentCreateDto announcmentCreateDto) {
        Announcment announcment = announcmentMapper.toEntityWithSave(announcmentCreateDto);
        if(announcmentCreateDto.isTeamAnnouncment()) {
            Team team = teamsService.getTeamById(announcmentCreateDto.receiverId());
            sendAnnouncmentToTeam(announcment, team);
        }else{
            User user = userService.getUserById(announcmentCreateDto.receiverId());
            sendAnnouncmentToUser(announcment, user);
        }
    }

    private void sendAnnouncmentToUser(Announcment announcment, User user) {
        broker.convertAndSendToUser(user.getEmail(), "/queue/notifications", announcment);
    }


    private void sendAnnouncmentToTeam(Announcment announcment, Team team) {
     broker.convertAndSend("/topic/teams/" + team.getId() + "/announcments", announcment);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userAnnouncements", key = "#userId")
    public List<AnnouncementResponseDto> getAllAnnouncmentsForUser(Long userId) {
       List<Announcment>announcments=announcmentUserRepository.findByUserId(userId)
                .stream()
                .map(AnnouncmentUser::getAnnouncment)
                .toList();
       return announcments.stream()
               .map(announcment -> announcmentMapper.toUserDto(announcment, userId))
               .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "teamAnnouncements", key = "#teamId")
    public List<AnnouncementResponseDto> getAllAnnouncmentsForTeam(Long teamId) {
      List<Announcment>announcments=announcmentTeamRepository.findByTeamId(teamId)
                .stream()
                .map(AnnouncmentTeam::getAnnouncment)
                .toList();
       return announcments.stream()
               .map(announcment -> announcmentMapper.toTeamDto(announcment, teamId))
               .toList();
    }


}
