package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Tag;

import java.util.List;

public interface TagService {
    Tag getTagEntityById(Long id);

    TagResponseDto createTag(TagCreateDto dto);

    TagResponseDto updateTag(Long id, TagUpdateDto dto);

    TagResponseDto getTagById(Long id);

    List<TagResponseDto> getAllTags();

    void deleteTag(Long id);
}
