package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Tag;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TagService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Tag;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpTagService implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    @Transactional(readOnly = true)
    public Tag getTagEntityById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "allTags", allEntries = true)
    public TagResponseDto createTag(TagCreateDto dto) {
        tagRepository.findByTagName(dto.tagName()).ifPresent(tag -> {
            throw new RuntimeException("Tag already exists with name: " + dto.tagName());
        });
        Tag tag = tagMapper.toEntity(dto);
        return tagMapper.toDto(tagRepository.save(tag));
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "tags", key = "#id"),
        @CacheEvict(value = "allTags", allEntries = true)
    })
    public TagResponseDto updateTag(Long id, TagUpdateDto dto) {
        Tag tag = getTagEntityById(id);
        tagRepository.findByTagName(dto.tagName()).ifPresent(existingTag -> {
            if (!existingTag.getId().equals(id)) {
                throw new RuntimeException("Tag already exists with name: " + dto.tagName());
            }
        });
        tagMapper.updateEntity(tag, dto);
        return tagMapper.toDto(tagRepository.save(tag));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tags", key = "#id")
    public TagResponseDto getTagById(Long id) {
        return tagMapper.toDto(getTagEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allTags")
    public List<TagResponseDto> getAllTags() {
        return tagRepository.findAll()
                .stream()
                .map(tagMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "tags", key = "#id"),
        @CacheEvict(value = "allTags", allEntries = true)
    })
    public void deleteTag(Long id) {
        Tag tag = getTagEntityById(id);
        tagRepository.delete(tag);
    }
}
