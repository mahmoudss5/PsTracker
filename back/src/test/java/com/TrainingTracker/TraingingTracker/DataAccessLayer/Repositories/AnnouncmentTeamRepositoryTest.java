package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Announcment;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentTeam;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey.AnnouncmnentTeamId;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.AnnouncmentType;
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
class AnnouncmentTeamRepositoryTest {

    @Autowired
    private AnnouncmentTeamRepository announcmentTeamRepository;

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

    private Announcment createTestAnnouncement(User sender, String content) {
        return Announcment.builder()
                .sender(sender)
                .content(content)
                .type(AnnouncmentType.INFO)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testFindByTeamId() {
        User sender = createTestUser("sender", "s@example.com");
        entityManager.persist(sender);

        Team team = createTestTeam("Alpha", "A123");
        entityManager.persist(team);

        Announcment ann = createTestAnnouncement(sender, "Maintenance announcement");
        entityManager.persist(ann);

        entityManager.flush();

        // Create composite key
        AnnouncmnentTeamId compositeId = new AnnouncmnentTeamId();
        compositeId.setAnnouncmentId(ann.getId());
        compositeId.setTeamId(team.getId());

        // Create relationship entity
        AnnouncmentTeam annTeam = AnnouncmentTeam.builder()
                .announcmnetTeamId(compositeId)
                .announcment(ann)
                .team(team)
                .build();

        entityManager.persistAndFlush(annTeam);

        List<AnnouncmentTeam> results = announcmentTeamRepository.findByTeamId(team.getId());
        assertEquals(1, results.size());
        assertEquals("Maintenance announcement", results.get(0).getAnnouncment().getContent());
    }
}
