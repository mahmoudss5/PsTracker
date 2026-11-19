package com.TrainingTracker.TraingingTracker.unit.controller;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ProblemService;
import com.TrainingTracker.TraingingTracker.Controllers.ProblemController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemUpdateDto;
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
class ProblemControllerTest {

    @Mock
    private ProblemService problemService;

    @InjectMocks
    private ProblemController problemController;

    private ProblemResponseDto responseDto() {
        return new ProblemResponseDto(1L, "A", "Shortest Path", 100L, 1200, List.of("graphs"));
    }

    @Test
    void testCreateProblem() {
        ProblemCreateDto dto = new ProblemCreateDto("A", "Shortest Path", 100L, 1200, List.of("graphs"));
        ProblemResponseDto expected = responseDto();
        when(problemService.createProblem(dto)).thenReturn(expected);

        ResponseEntity<ProblemResponseDto> response = problemController.createProblem(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(problemService).createProblem(dto);
    }

    @Test
    void testGetAllProblems() {
        List<ProblemResponseDto> dtoList = List.of(responseDto());
        when(problemService.getAllProblems()).thenReturn(dtoList);

        ResponseEntity<List<ProblemResponseDto>> response = problemController.getAllProblems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(problemService).getAllProblems();
    }

    @Test
    void testGetProblemById() {
        Long problemId = 1L;
        ProblemResponseDto expected = responseDto();
        when(problemService.getProblemById(problemId)).thenReturn(expected);

        ResponseEntity<ProblemResponseDto> response = problemController.getProblemById(problemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(problemService).getProblemById(problemId);
    }

    @Test
    void testUpdateProblem() {
        Long problemId = 1L;
        ProblemUpdateDto dto = new ProblemUpdateDto("A", "Shortest Path", 100L, 1400, List.of("graphs"));
        ProblemResponseDto responseDto = new ProblemResponseDto(problemId, "A", "Shortest Path", 100L, 1400, List.of("graphs"));
        when(problemService.updateProblem(problemId, dto)).thenReturn(responseDto);

        ResponseEntity<ProblemResponseDto> response = problemController.updateProblem(problemId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(problemService).updateProblem(problemId, dto);
    }

    @Test
    void testDeleteProblem() {
        Long problemId = 1L;

        ResponseEntity<Void> response = problemController.deleteProblem(problemId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(problemService).deleteProblem(problemId);
    }
}
