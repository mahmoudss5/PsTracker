package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.TagService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Tag.TagUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Create tag")
    @PostMapping
    public ResponseEntity<TagResponseDto> createTag(@RequestBody TagCreateDto dto) {
        return ResponseEntity.ok(tagService.createTag(dto));
    }

    @Operation(summary = "Get all tags")
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @Operation(summary = "Get tag by id")
    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @Operation(summary = "Update tag")
    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDto> updateTag(
            @PathVariable Long id,
            @RequestBody TagUpdateDto dto
    ) {
        return ResponseEntity.ok(tagService.updateTag(id, dto));
    }

    @Operation(summary = "Delete tag")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
