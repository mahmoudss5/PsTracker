package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
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
class SubmissionRepositoryTest {

    @Autowired
    private SubmissionRepository submissionRepository;

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

    private Problem createTestProblem(String name) {
        return Problem.builder()
                .name(name)
                .problemIndex("A")
                .contestId(1L)
                .rating(800)
                .build();
    }

    private Submission createTestSubmission(User user, Problem problem, String verdict, Long codeforcesId, LocalDateTime createdAt) {
        return Submission.builder()
                .user(user)
                .problem(problem)
                .verdict(verdict)
                .codeforcesSubmissionId(codeforcesId)
                .createdAt(createdAt)
                .timeConsumedMs(100)
                .memoryConsumedBytes(1024)
                .build();
    }

    @Test
    void testCountSolvedProblemsByUserId() {
        User user = createTestUser("trainee1", "t1@example.com");
        entityManager.persist(user);

        Problem prob1 = createTestProblem("Watermelon");
        Problem prob2 = createTestProblem("Theatre Square");
        entityManager.persist(prob1);
        entityManager.persist(prob2);

        // Submissions for user
        Submission sub1 = createTestSubmission(user, prob1, "OK", 100L, LocalDateTime.now());
        Submission sub2 = createTestSubmission(user, prob1, "ACCEPTED", 101L, LocalDateTime.now()); // duplicate problem solve
        Submission sub3 = createTestSubmission(user, prob2, "WRONG_ANSWER", 102L, LocalDateTime.now());
        Submission sub4 = createTestSubmission(user, prob2, "ok", 103L, LocalDateTime.now()); // solved, check casing

        entityManager.persist(sub1);
        entityManager.persist(sub2);
        entityManager.persist(sub3);
        entityManager.persist(sub4);
        entityManager.flush();

        Long solvedCount = submissionRepository.countSolvedProblemsByUserId(user.getId());
        assertEquals(2L, solvedCount); // Watermelon and Theatre Square (prob1 and prob2)
    }

    @Test
    void testFindByUserIdOrderByCreatedAtDesc() {
        User user = createTestUser("trainee", "t@example.com");
        entityManager.persist(user);

        Problem problem = createTestProblem("Watermelon");
        entityManager.persist(problem);

        LocalDateTime now = LocalDateTime.now();
        Submission sub1 = createTestSubmission(user, problem, "WRONG_ANSWER", 100L, now.minusHours(1));
        Submission sub2 = createTestSubmission(user, problem, "OK", 101L, now);

        entityManager.persist(sub1);
        entityManager.persist(sub2);
        entityManager.flush();

        List<Submission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        assertEquals(2, submissions.size());
        assertEquals(101L, submissions.get(0).getCodeforcesSubmissionId()); // latest first
    }

    @Test
    void testFindByProblemIdOrderByCreatedAtDesc() {
        User user = createTestUser("trainee", "t@example.com");
        entityManager.persist(user);

        Problem problem = createTestProblem("Watermelon");
        entityManager.persist(problem);

        LocalDateTime now = LocalDateTime.now();
        Submission sub1 = createTestSubmission(user, problem, "WRONG_ANSWER", 100L, now.minusHours(1));
        Submission sub2 = createTestSubmission(user, problem, "OK", 101L, now);

        entityManager.persist(sub1);
        entityManager.persist(sub2);
        entityManager.flush();

        List<Submission> submissions = submissionRepository.findByProblemIdOrderByCreatedAtDesc(problem.getId());
        assertEquals(2, submissions.size());
        assertEquals(101L, submissions.get(0).getCodeforcesSubmissionId());
    }

    @Test
    void testFindAcceptedSubmissionsByUserAndDateRange() {
        User user = createTestUser("trainee", "t@example.com");
        entityManager.persist(user);

        Problem problem = createTestProblem("Watermelon");
        entityManager.persist(problem);

        LocalDateTime start = LocalDateTime.of(2026, 6, 24, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 25, 0, 0);

        Submission subBefore = createTestSubmission(user, problem, "OK", 99L, start.minusSeconds(1));
        Submission subWithinOk = createTestSubmission(user, problem, "OK", 100L, start.plusHours(2));
        Submission subWithinAccepted = createTestSubmission(user, problem, "accepted", 101L, start.plusHours(4));
        Submission subWithinWa = createTestSubmission(user, problem, "WRONG_ANSWER", 102L, start.plusHours(6));
        Submission subAfter = createTestSubmission(user, problem, "OK", 103L, end);

        entityManager.persist(subBefore);
        entityManager.persist(subWithinOk);
        entityManager.persist(subWithinAccepted);
        entityManager.persist(subWithinWa);
        entityManager.persist(subAfter);
        entityManager.flush();

        List<Submission> acceptedSubmissions = submissionRepository.findAcceptedSubmissionsByUserAndDateRange(user.getId(), start, end);
        assertEquals(2, acceptedSubmissions.size());
        assertTrue(acceptedSubmissions.stream().anyMatch(s -> s.getCodeforcesSubmissionId().equals(100L)));
        assertTrue(acceptedSubmissions.stream().anyMatch(s -> s.getCodeforcesSubmissionId().equals(101L)));
    }

    @Test
    void testExistsByCodeforcesSubmissionId() {
        User user = createTestUser("trainee", "t@example.com");
        entityManager.persist(user);

        Problem problem = createTestProblem("Watermelon");
        entityManager.persist(problem);

        Submission sub = createTestSubmission(user, problem, "OK", 12345L, LocalDateTime.now());
        entityManager.persistAndFlush(sub);

        assertTrue(submissionRepository.existsByCodeforcesSubmissionId(12345L));
        assertFalse(submissionRepository.existsByCodeforcesSubmissionId(99999L));
    }
}
