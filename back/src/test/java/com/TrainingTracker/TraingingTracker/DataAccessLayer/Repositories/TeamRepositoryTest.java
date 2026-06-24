package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

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
    void testFindByTeamName() {
        Team team = createTestTeam("Alpha", "A123");
        entityManager.persistAndFlush(team);

        Team found = teamRepository.findByTeamName("Alpha");
        assertNotNull(found);
        assertEquals("A123", found.getTeamCode());
    }

    @Test
    void testFindByTeamCode() {
        Team team = createTestTeam("Beta", "B123");
        entityManager.persistAndFlush(team);

        Optional<Team> found = teamRepository.findByTeamCode("B123");
        assertTrue(found.isPresent());
        assertEquals("Beta", found.get().getTeamName());
    }

    @Test
    void testExistsByTeamCode() {
        Team team = createTestTeam("Gamma", "G123");
        entityManager.persistAndFlush(team);

        assertTrue(teamRepository.existsByTeamCode("G123"));
        assertFalse(teamRepository.existsByTeamCode("NON_EXISTENT"));
    }

    @Test
    void testFindTeamNameByUserId() {
        Team team = createTestTeam("Delta", "D123");
        entityManager.persist(team);

        User user = createTestUser("trainee1", "trainee1@example.com");
        user.setTraineeTeam(team);
        entityManager.persist(user);

        entityManager.flush();

        String teamName = teamRepository.findTeamNameByUserId(user.getId());
        assertEquals("Delta", teamName);
    }
}
