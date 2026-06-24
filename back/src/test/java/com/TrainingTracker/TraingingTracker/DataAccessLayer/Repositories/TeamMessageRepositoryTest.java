package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.TeamMessage;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.Role;
import com.TrainingTracker.TraingingTracker.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class TeamMessageRepositoryTest {

    @Autowired
    private TeamMessageRepository teamMessageRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User createTestUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .codeforcesHandle(username + "_cf")
                .password("securePassword123")
                .role(Role.Trainee)
                .rate(1200L)
                .maxRate(1400L)
                .rank("Newbie")
                .maxRank("Pupil")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Team createTestTeam(String teamName, String teamCode) {
        return Team.builder()
                .teamName(teamName)
                .teamCode(teamCode)
                .build();
    }

    @Test
    void testFindByTeamId() {
        User sender = createTestUser("sender", "s@example.com");
        entityManager.persist(sender);

        Team team = createTestTeam("Alpha", "A123");
        entityManager.persist(team);

        TeamMessage message = TeamMessage.builder()
                .sender(sender)
                .team(team)
                .content("Hello Team Alpha!")
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(message);

        List<TeamMessage> messages = teamMessageRepository.findByTeamId(team.getId());
        assertEquals(1, messages.size());
        assertEquals("Hello Team Alpha!", messages.get(0).getContent());
        assertEquals(sender.getUsername(), messages.get(0).getSender().getUsername());
    }
}
