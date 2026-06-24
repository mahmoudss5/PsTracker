package com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Tag;
import com.TrainingTracker.TraingingTracker.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Tag createTestTag(String name) {
        return Tag.builder().tagName(name).build();
    }

    @Test
    void testFindByTagName() {
        Tag tag = createTestTag("dp");
        entityManager.persistAndFlush(tag);

        Optional<Tag> found = tagRepository.findByTagName("dp");
        assertTrue(found.isPresent());
        assertEquals("dp", found.get().getTagName());
    }

    @Test
    void testFindByTagNameIn() {
        Tag tag1 = createTestTag("graphs");
        Tag tag2 = createTestTag("math");
        Tag tag3 = createTestTag("strings");
        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(tag3);
        entityManager.flush();

        List<Tag> found = tagRepository.findByTagNameIn(List.of("graphs", "math", "other"));
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(t -> t.getTagName().equals("graphs")));
        assertTrue(found.stream().anyMatch(t -> t.getTagName().equals("math")));
    }
}
