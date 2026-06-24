package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Tag;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Tag;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpTagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private ImpTagService tagService;

    @Test
    void testGetTagEntityById_Success() {
        Long id = 1L;
        Tag tag = Tag.builder().id(id).tagName("dp").build();
        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));

        Tag result = tagService.getTagEntityById(id);

        assertNotNull(result);
        assertEquals("dp", result.getTagName());
    }

    @Test
    void testGetTagEntityById_NotFound() {
        Long id = 999L;
        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> tagService.getTagEntityById(id));
        assertEquals("Tag not found with id: " + id, exception.getMessage());
    }

    @Test
    void testCreateTag_Success() {
        TagCreateDto createDto = new TagCreateDto("dp");
        Tag tag = Tag.builder().id(10L).tagName("dp").build();
        TagResponseDto responseDto = new TagResponseDto(10L, "dp");

        when(tagRepository.findByTagName("dp")).thenReturn(Optional.empty());
        when(tagMapper.toEntity(createDto)).thenReturn(tag);
        when(tagRepository.save(tag)).thenReturn(tag);
        when(tagMapper.toDto(tag)).thenReturn(responseDto);

        TagResponseDto actual = tagService.createTag(createDto);

        assertNotNull(actual);
        assertEquals(10L, actual.id());
        verify(tagRepository).findByTagName("dp");
        verify(tagRepository).save(tag);
    }

    @Test
    void testCreateTag_AlreadyExists() {
        TagCreateDto createDto = new TagCreateDto("dp");
        Tag tag = Tag.builder().id(10L).tagName("dp").build();

        when(tagRepository.findByTagName("dp")).thenReturn(Optional.of(tag));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> tagService.createTag(createDto));
        assertEquals("Tag already exists with name: dp", exception.getMessage());
        verify(tagRepository, never()).save(any());
    }

    @Test
    void testUpdateTag_Success() {
        Long id = 10L;
        TagUpdateDto updateDto = new TagUpdateDto("graphs");
        Tag tag = Tag.builder().id(id).tagName("dp").build();
        TagResponseDto responseDto = new TagResponseDto(id, "graphs");

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));
        when(tagRepository.findByTagName("graphs")).thenReturn(Optional.empty());
        when(tagRepository.save(tag)).thenReturn(tag);
        when(tagMapper.toDto(tag)).thenReturn(responseDto);

        TagResponseDto actual = tagService.updateTag(id, updateDto);

        assertNotNull(actual);
        assertEquals(id, actual.id());
        verify(tagMapper).updateEntity(tag, updateDto);
    }

    @Test
    void testUpdateTag_Conflict() {
        Long id = 10L;
        TagUpdateDto updateDto = new TagUpdateDto("graphs");
        Tag tag = Tag.builder().id(id).tagName("dp").build();
        Tag otherTag = Tag.builder().id(20L).tagName("graphs").build();

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));
        when(tagRepository.findByTagName("graphs")).thenReturn(Optional.of(otherTag));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> tagService.updateTag(id, updateDto));
        assertEquals("Tag already exists with name: graphs", exception.getMessage());
        verify(tagRepository, never()).save(any());
    }

    @Test
    void testGetTagById() {
        Long id = 10L;
        Tag tag = Tag.builder().id(id).tagName("dp").build();
        TagResponseDto responseDto = new TagResponseDto(id, "dp");

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));
        when(tagMapper.toDto(tag)).thenReturn(responseDto);

        TagResponseDto actual = tagService.getTagById(id);

        assertNotNull(actual);
        assertEquals(id, actual.id());
    }

    @Test
    void testGetAllTags() {
        Tag tag = Tag.builder().id(10L).build();
        TagResponseDto responseDto = new TagResponseDto(10L, "dp");

        when(tagRepository.findAll()).thenReturn(List.of(tag));
        when(tagMapper.toDto(tag)).thenReturn(responseDto);

        List<TagResponseDto> results = tagService.getAllTags();

        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).id());
    }

    @Test
    void testDeleteTag() {
        Long id = 10L;
        Tag tag = Tag.builder().id(id).build();

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));

        tagService.deleteTag(id);

        verify(tagRepository).delete(tag);
    }
}
