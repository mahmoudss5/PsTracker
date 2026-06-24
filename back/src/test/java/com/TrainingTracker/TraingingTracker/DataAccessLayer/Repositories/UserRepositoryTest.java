package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.Role;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.ContextConfiguration;
import com.TrainingTracker.TraingingTracker.TestApplication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User createTestUser(String username, String email, String codeforcesHandle) {
        return User.builder()
                .username(username)
                .email(email)
                .codeforcesHandle(codeforcesHandle)
                .password("securePassword123")
                .role(Role.Trainee)
                .rate(1200L)
                .maxRate(1400L)
                .rank("Newbie")
                .maxRank("Pupil")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testSaveAndFindById() {
        User user = createTestUser("alice", "alice@example.com", "alice_cf");

        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId(), "Saved user should have an ID assigned");

        Optional<User> found = userRepository.findById(savedUser.getId());
        assertTrue(found.isPresent(), "User should be found by ID");
        assertEquals("alice", found.get().getUsername());
        assertEquals("alice@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail_Success() {
        User user = createTestUser("bob", "bob@example.com", "bob_cf");
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("bob@example.com");
        assertTrue(found.isPresent(), "User should be found by email");
        assertEquals("bob", found.get().getUsername());
    }

    @Test
    void testFindByEmail_NotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertFalse(found.isPresent(), "User should not be found for nonexistent email");
    }

    @Test
    void testFindByCodeforcesHandle_Success() {
        User user = createTestUser("charlie", "charlie@example.com", "charlie_cf");
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByCodeforcesHandle("charlie_cf");
        assertTrue(found.isPresent(), "User should be found by codeforces handle");
        assertEquals("charlie", found.get().getUsername());
    }

    @Test
    void testFindByCodeforcesHandle_NotFound() {
        Optional<User> found = userRepository.findByCodeforcesHandle("nonexistent_cf");
        assertFalse(found.isPresent(), "User should not be found for nonexistent codeforces handle");
    }

    @Test
    void testUniqueEmailConstraint() {
        User user1 = createTestUser("user1", "duplicate@example.com", "handle1");
        User user2 = createTestUser("user2", "duplicate@example.com", "handle2");

        entityManager.persistAndFlush(user1);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
            entityManager.flush();
        }, "Should throw DataIntegrityViolationException when email is duplicated");
    }

    @Test
    void testUniqueCodeforcesHandleConstraint() {
        User user1 = createTestUser("user1", "user1@example.com", "duplicate_cf");
        User user2 = createTestUser("user2", "user2@example.com", "duplicate_cf");

        entityManager.persistAndFlush(user1);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
            entityManager.flush();
        }, "Should throw DataIntegrityViolationException when codeforces handle is duplicated");
    }
}