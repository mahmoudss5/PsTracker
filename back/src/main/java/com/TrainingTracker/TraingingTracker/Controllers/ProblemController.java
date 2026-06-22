package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ProblemService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemUpdateDto;
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
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @Operation(summary = "Create problem")
    @PostMapping
    public ResponseEntity<ProblemResponseDto> createProblem(@RequestBody ProblemCreateDto dto) {
        return ResponseEntity.ok(problemService.createProblem(dto));
    }

    @Operation(summary = "Get all problems")
    @GetMapping
    public ResponseEntity<List<ProblemResponseDto>> getAllProblems() {
        return ResponseEntity.ok(problemService.getAllProblems());
    }

    @Operation(summary = "Get problem by id")
    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponseDto> getProblemById(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getProblemById(id));
    }

    @Operation(summary = "Update problem")
    @PutMapping("/{id}")
    public ResponseEntity<ProblemResponseDto> updateProblem(
            @PathVariable Long id,
            @RequestBody ProblemUpdateDto dto
    ) {
        return ResponseEntity.ok(problemService.updateProblem(id, dto));
    }

    @Operation(summary = "Delete problem")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
