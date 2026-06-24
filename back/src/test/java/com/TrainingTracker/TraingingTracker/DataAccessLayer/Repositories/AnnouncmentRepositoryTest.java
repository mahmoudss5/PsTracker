package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Announcment;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
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
class AnnouncmentRepositoryTest {

    @Autowired
    private AnnouncmentRepository announcmentRepository;

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

    private Announcment createTestAnnouncement(User sender, String content, AnnouncmentType type, String status) {
        return Announcment.builder()
                .sender(sender)
                .content(content)
                .type(type)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testFindBySenderId() {
        User sender = createTestUser("sender1", "s1@example.com");
        entityManager.persist(sender);

        Announcment ann = createTestAnnouncement(sender, "Maintenance", AnnouncmentType.WARNING, "ACTIVE");
        entityManager.persist(ann);
        entityManager.flush();

        List<Announcment> announcements = announcmentRepository.findBySenderId(sender.getId());
        assertEquals(1, announcements.size());
        assertEquals("Maintenance", announcements.get(0).getContent());
    }

    @Test
    void testFindByType() {
        User sender = createTestUser("sender", "s@example.com");
        entityManager.persist(sender);

        Announcment ann1 = createTestAnnouncement(sender, "Info 1", AnnouncmentType.INFO, "ACTIVE");
        Announcment ann2 = createTestAnnouncement(sender, "Error 1", AnnouncmentType.ERROR, "ACTIVE");
        entityManager.persist(ann1);
        entityManager.persist(ann2);
        entityManager.flush();

        List<Announcment> infoAnnouncements = announcmentRepository.findByType(AnnouncmentType.INFO);
        assertEquals(1, infoAnnouncements.size());
        assertEquals("Info 1", infoAnnouncements.get(0).getContent());
    }

    @Test
    void testFindByStatus() {
        User sender = createTestUser("sender", "s@example.com");
        entityManager.persist(sender);

        Announcment ann1 = createTestAnnouncement(sender, "Content 1", AnnouncmentType.INFO, "ACTIVE");
        Announcment ann2 = createTestAnnouncement(sender, "Content 2", AnnouncmentType.INFO, "INACTIVE");
        entityManager.persist(ann1);
        entityManager.persist(ann2);
        entityManager.flush();

        List<Announcment> activeAnnouncements = announcmentRepository.findByStatus("ACTIVE");
        assertEquals(1, activeAnnouncements.size());
        assertEquals("Content 1", activeAnnouncements.get(0).getContent());
    }

    @Test
    void testFindBySenderIdAndStatus() {
        User sender = createTestUser("sender", "s@example.com");
        entityManager.persist(sender);

        Announcment ann1 = createTestAnnouncement(sender, "Content 1", AnnouncmentType.INFO, "ACTIVE");
        Announcment ann2 = createTestAnnouncement(sender, "Content 2", AnnouncmentType.INFO, "INACTIVE");
        entityManager.persist(ann1);
        entityManager.persist(ann2);
        entityManager.flush();

        List<Announcment> activeSenderAnnouncements = announcmentRepository.findBySenderIdAndStatus(sender.getId(), "ACTIVE");
        assertEquals(1, activeSenderAnnouncements.size());
        assertEquals("Content 1", activeSenderAnnouncements.get(0).getContent());
    }
}
