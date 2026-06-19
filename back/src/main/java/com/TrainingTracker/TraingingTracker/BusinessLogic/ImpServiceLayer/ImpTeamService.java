package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TeamsService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpTeamService implements TeamsService {

    private final TeamRepository teamRepository;
    private final UserService userService;
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;


    @Override
    public String createTeam(String teamName) {
        Long coachId = SecuiryUserUtil.getCurrntUserId();
        User coach = userService.getUserById(coachId);
        String teamCode;
        do {
            teamCode = GenerateRandomTeamCode();
        } while (teamRepository.existsByTeamCode(teamCode));
        Team team = Team.builder().teamCode(teamCode).teamName(teamName).coach(coach).build();
        return teamCode;
    }

    @Override
    public void JoinTeam(String teamCode) {
        Team team = teamRepository.findByTeamCode(teamCode).orElseThrow(() -> new RuntimeException("Team not found with code: " + teamCode));
        if (!team.getTeamCode().equals(teamCode)) throw new RuntimeException("Team code is not valid");
        if (team.getTrainees().size() >= 4) throw new RuntimeException("Team is full");
        Long traineeId = SecuiryUserUtil.getCurrntUserId();
        User trainee = userService.getUserById(traineeId);
        team.getTrainees().add(trainee);
        teamRepository.save(team);
    }

    @Override
    public void leaveTeam(Long teamId) {
        Long userId = SecuiryUserUtil.getCurrntUserId();
        User user = userService.getUserById(userId);
      Team team = teamRepository.findById(teamId)
              .orElseThrow(()-> new RuntimeException("Team not found with id: "+teamId));
      team.getTrainees().remove( user);
      teamRepository.save(team);
    }


    private String GenerateRandomTeamCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH / 2; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        for (int i = 0; i < CODE_LENGTH / 2; i++) {
            int temp = random.nextInt(10);
            sb.append(Integer.toString(temp));
        }
        return sb.toString();
    }
}
