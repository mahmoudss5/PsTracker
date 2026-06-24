package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Announcment;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.AnnouncmentUser;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey.AnnouncmentUserId;
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
class AnnouncmentUserRepositoryTest {

    @Autowired
    private AnnouncmentUserRepository announcmentUserRepository;

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
    void testFindByUserId() {
        User sender = createTestUser("sender", "s@example.com");
        entityManager.persist(sender);

        User targetUser = createTestUser("target", "target@example.com");
        entityManager.persist(targetUser);

        Announcment ann = createTestAnnouncement(sender, "User specific announcement");
        entityManager.persist(ann);

        entityManager.flush();

        // Create composite key
        AnnouncmentUserId compositeId = new AnnouncmentUserId();
        compositeId.setAnnouncmentId(ann.getId());
        compositeId.setUserId(targetUser.getId());

        // Create relationship entity
        AnnouncmentUser annUser = AnnouncmentUser.builder()
                .announcmentUserId(compositeId)
                .announcment(ann)
                .user(targetUser)
                .build();

        entityManager.persistAndFlush(annUser);

        List<AnnouncmentUser> results = announcmentUserRepository.findByUserId(targetUser.getId());
        assertEquals(1, results.size());
        assertEquals("User specific announcement", results.get(0).getAnnouncment().getContent());
    }
}
