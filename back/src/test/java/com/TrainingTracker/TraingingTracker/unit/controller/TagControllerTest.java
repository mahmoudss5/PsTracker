package com.TrainingTracker.TraingingTracker.unit.controller;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TagService;
import com.TrainingTracker.TraingingTracker.Controllers.TagController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagUpdateDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    private TagResponseDto responseDto() {
        return new TagResponseDto(1L, "graphs");
    }

    @Test
    void testCreateTag() {
        TagCreateDto dto = new TagCreateDto("graphs");
        TagResponseDto expected = responseDto();
        when(tagService.createTag(dto)).thenReturn(expected);

        ResponseEntity<TagResponseDto> response = tagController.createTag(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(tagService).createTag(dto);
    }

    @Test
    void testGetAllTags() {
        List<TagResponseDto> dtoList = List.of(responseDto());
        when(tagService.getAllTags()).thenReturn(dtoList);

        ResponseEntity<List<TagResponseDto>> response = tagController.getAllTags();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(tagService).getAllTags();
    }

    @Test
    void testGetTagById() {
        Long tagId = 1L;
        TagResponseDto expected = responseDto();
        when(tagService.getTagById(tagId)).thenReturn(expected);

        ResponseEntity<TagResponseDto> response = tagController.getTagById(tagId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(tagService).getTagById(tagId);
    }

    @Test
    void testUpdateTag() {
        Long tagId = 1L;
        TagUpdateDto dto = new TagUpdateDto("trees");
        TagResponseDto responseDto = new TagResponseDto(tagId, "trees");
        when(tagService.updateTag(tagId, dto)).thenReturn(responseDto);

        ResponseEntity<TagResponseDto> response = tagController.updateTag(tagId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(tagService).updateTag(tagId, dto);
    }

    @Test
    void testDeleteTag() {
        Long tagId = 1L;

        ResponseEntity<Void> response = tagController.deleteTag(tagId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(tagService).deleteTag(tagId);
    }
}
