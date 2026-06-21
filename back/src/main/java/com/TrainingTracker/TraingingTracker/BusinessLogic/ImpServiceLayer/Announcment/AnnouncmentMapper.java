package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Announcment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Announcment;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncmentCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.AnnouncmentType;
import java.time.LocalDateTime;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentTeam;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentTeamRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentUserRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.AnnouncmentRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentUser;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncementResponseDto;

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
        Announcment announcment =  Announcment.builder()
        .sender(sender)
        .content(announcmentCreateDto.content())
        .type(AnnouncmentType.valueOf(announcmentCreateDto.type()))
        .createdAt(LocalDateTime.now())
        .status("ACTIVE")
        .build();
        
        announcmentRepository.save(announcment);

        AnnouncmentTeam announcmentTeam = AnnouncmentTeam.builder()
        .announcment(announcment)
        .team(team)
        .build();
        announcmentTeamRepository.save(announcmentTeam);
        return announcment;
    }




     private Announcment handleUserAnnouncment(AnnouncmentCreateDto announcmentCreateDto, User sender) {
        User user = userService.getUserById(announcmentCreateDto.receiverId());
        if(user == null) throw new RuntimeException("User not found");
        Announcment announcment =  Announcment.builder()
        .sender(sender)
        .content(announcmentCreateDto.content())
        .type(AnnouncmentType.valueOf(announcmentCreateDto.type()))
        .createdAt(LocalDateTime.now())
        .status("ACTIVE")
        .build();
        announcmentRepository.save(announcment);

        AnnouncmentUser announcmentUser = AnnouncmentUser.builder()
        .announcment(announcment)
        .user(user)
        .build();
        announcmentUserRepository.save(announcmentUser);
        return announcment; 
     }
    
    public Announcment toEntityWithSave(AnnouncmentCreateDto announcmentCreateDto) {
        User sender = userService.getUserById(announcmentCreateDto.senderId());
        if(sender == null) throw new RuntimeException("Sender not found");
         
        Announcment announcment = new Announcment();
        if(announcmentCreateDto.isTeamAnnouncment()) {
           
            announcment = handleTeamAnnouncment(announcmentCreateDto, sender);
         }else{
            announcment = handleUserAnnouncment(announcmentCreateDto, sender);
        }
        return announcment;
    }


    public AnnouncementResponseDto toUserDto(Announcment announcment,Long userId) {

        AnnouncementResponseDto announcementResponseDto = new AnnouncementResponseDto(
        announcment.getId(),
        announcment.getType().name(),
        announcment.getContent(),
        announcment.getSender().getId(),
        userId,
        announcment.getCreatedAt()
       );

     return announcementResponseDto;
    }
    
    public AnnouncementResponseDto toTeamDto(Announcment announcment,Long teamId){
        AnnouncementResponseDto announcementResponseDto=new AnnouncementResponseDto(
          announcment.getId(),
          announcment.getType().name(),
          announcment.getContent(),
           announcment.getSender().getId(),
          teamId,
        announcment.getCreatedAt()
        );
        return announcementResponseDto;
    }
}
