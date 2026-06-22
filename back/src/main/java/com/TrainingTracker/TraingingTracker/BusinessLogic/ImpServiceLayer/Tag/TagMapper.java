package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Tag;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public Tag toEntity(TagCreateDto dto) {
        if (dto == null) {
            return null;
        }
        return Tag.builder()
                .tagName(dto.tagName())
                .build();
    }

    public TagResponseDto toDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        return new TagResponseDto(
                tag.getId(),
                tag.getTagName()
        );
    }

    public void updateEntity(Tag tag, TagUpdateDto dto) {
        if (tag == null || dto == null) {
            return;
        }
        tag.setTagName(dto.tagName());
    }
}
