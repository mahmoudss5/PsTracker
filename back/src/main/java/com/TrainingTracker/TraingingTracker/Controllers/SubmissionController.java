package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.SubmissionService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @Operation(summary = "Create submission")
    @PostMapping
    public ResponseEntity<SubmissionResponseDto> createSubmission(@RequestBody SubmissionCreateDto dto) {
        return ResponseEntity.ok(submissionService.createSubmission(dto));
    }

    @Operation(summary = "Create submission for current user")
    @PostMapping("/me")
    public ResponseEntity<SubmissionResponseDto> createCurrentUserSubmission(@RequestBody SubmissionCreateDto dto) {
        return ResponseEntity.ok(submissionService.createCurrentUserSubmission(dto));
    }

    @Operation(summary = "Get all submissions")
    @GetMapping
    public ResponseEntity<List<SubmissionResponseDto>> getAllSubmissions() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }

    @Operation(summary = "Get current user submissions")
    @GetMapping("/me")
    public ResponseEntity<List<SubmissionResponseDto>> getCurrentUserSubmissions() {
        return ResponseEntity.ok(submissionService.getCurrentUserSubmissions());
    }

    @Operation(summary = "Get submissions by user id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubmissionResponseDto>> getSubmissionsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByUserId(userId));
    }

    @Operation(summary = "Get submissions by problem id")
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<List<SubmissionResponseDto>> getSubmissionsByProblemId(@PathVariable Long problemId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByProblemId(problemId));
    }

    @Operation(summary = "Get submission by id")
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponseDto> getSubmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.getSubmissionById(id));
    }

    @Operation(summary = "Update submission")
    @PutMapping("/{id}")
    public ResponseEntity<SubmissionResponseDto> updateSubmission(
            @PathVariable Long id,
            @RequestBody SubmissionUpdateDto dto
    ) {
        return ResponseEntity.ok(submissionService.updateSubmission(id, dto));
    }

    @Operation(summary = "Delete submission")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }
}
