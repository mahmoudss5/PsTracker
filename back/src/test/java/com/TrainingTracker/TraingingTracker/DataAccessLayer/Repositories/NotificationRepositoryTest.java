package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Notification;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.NotificationType;
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
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

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

    private Notification createTestNotification(User user, String title, String message, LocalDateTime createdAt) {
        return Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(NotificationType.TRAINEE_ASSIGNMENT_SUBMISSION)
                .isRead(false)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    @Test
    void testFindByUserIdOrderByCreatedAtDesc() {
        User user = createTestUser("trainee", "t@example.com");
        entityManager.persist(user);

        LocalDateTime now = LocalDateTime.now();
        Notification notif1 = createTestNotification(user, "Title 1", "Message 1", now.minusHours(2));
        Notification notif2 = createTestNotification(user, "Title 2", "Message 2", now.minusHours(1));

        entityManager.persist(notif1);
        entityManager.persist(notif2);
        entityManager.flush();

        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        assertEquals(2, notifications.size());
        assertEquals("Title 2", notifications.get(0).getTitle()); // latest first
    }
}
