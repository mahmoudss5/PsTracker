package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Problem createTestProblem(String name, String index, Long contestId, Integer rating) {
        return Problem.builder()
                .name(name)
                .problemIndex(index)
                .contestId(contestId)
                .rating(rating)
                .build();
    }

    @Test
    void testFindByName() {
        Problem problem = createTestProblem("Watermelon", "A", 4L, 800);
        entityManager.persistAndFlush(problem);

        Optional<Problem> found = problemRepository.findByName("Watermelon");
        assertTrue(found.isPresent());
        assertEquals("A", found.get().getProblemIndex());
        assertEquals(800, found.get().getRating());
    }

    @Test
    void testExistsByName() {
        Problem problem = createTestProblem("Theatre Square", "A", 1L, 1000);
        entityManager.persistAndFlush(problem);

        assertTrue(problemRepository.existsByName("Theatre Square"));
        assertFalse(problemRepository.existsByName("NON_EXISTENT"));
    }
}
