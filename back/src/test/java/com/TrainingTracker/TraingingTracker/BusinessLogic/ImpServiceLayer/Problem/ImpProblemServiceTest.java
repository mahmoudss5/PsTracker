package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Problem;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ProblemRepository;
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
class ImpProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private ProblemMapper problemMapper;

    @InjectMocks
    private ImpProblemService problemService;

    @Test
    void testGetProblemEntityById_Success() {
        Long id = 1L;
        Problem problem = Problem.builder().id(id).name("Watermelon").build();
        when(problemRepository.findById(id)).thenReturn(Optional.of(problem));

        Problem result = problemService.getProblemEntityById(id);

        assertNotNull(result);
        assertEquals("Watermelon", result.getName());
    }

    @Test
    void testGetProblemEntityById_NotFound() {
        Long id = 999L;
        when(problemRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> problemService.getProblemEntityById(id));
        assertEquals("Problem not found with id: " + id, exception.getMessage());
    }

    @Test
    void testCreateProblem_Success() {
        ProblemCreateDto createDto = new ProblemCreateDto("A", "Watermelon", 4L, 800, List.of("math"));
        Problem problem = Problem.builder().id(10L).name("Watermelon").build();
        ProblemResponseDto responseDto = new ProblemResponseDto(10L, "A", "Watermelon", 4L, 800, List.of("math"));

        when(problemRepository.findByName("Watermelon")).thenReturn(Optional.empty());
        when(problemMapper.toEntity(createDto)).thenReturn(problem);
        when(problemRepository.save(problem)).thenReturn(problem);
        when(problemMapper.toDto(problem)).thenReturn(responseDto);

        ProblemResponseDto actual = problemService.createProblem(createDto);

        assertNotNull(actual);
        assertEquals(10L, actual.id());
        verify(problemRepository).findByName("Watermelon");
        verify(problemRepository).save(problem);
    }

    @Test
    void testCreateProblem_AlreadyExists() {
        ProblemCreateDto createDto = new ProblemCreateDto("A", "Watermelon", 4L, 800, List.of("math"));
        Problem problem = Problem.builder().id(10L).name("Watermelon").build();

        when(problemRepository.findByName("Watermelon")).thenReturn(Optional.of(problem));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> problemService.createProblem(createDto));
        assertEquals("Problem already exists with name: Watermelon", exception.getMessage());
        verify(problemRepository, never()).save(any());
    }

    @Test
    void testUpdateProblem() {
        Long id = 10L;
        ProblemUpdateDto updateDto = new ProblemUpdateDto("B", "Watermelon", 4L, 900, List.of("math"));
        Problem problem = Problem.builder().id(id).name("Watermelon").build();
        ProblemResponseDto responseDto = new ProblemResponseDto(id, "B", "Watermelon", 4L, 900, List.of("math"));

        when(problemRepository.findById(id)).thenReturn(Optional.of(problem));
        when(problemRepository.save(problem)).thenReturn(problem);
        when(problemMapper.toDto(problem)).thenReturn(responseDto);

        ProblemResponseDto actual = problemService.updateProblem(id, updateDto);

        assertNotNull(actual);
        assertEquals(id, actual.id());
        verify(problemMapper).updateEntity(problem, updateDto);
        verify(problemRepository).save(problem);
    }

    @Test
    void testGetProblemById() {
        Long id = 10L;
        Problem problem = Problem.builder().id(id).name("Watermelon").build();
        ProblemResponseDto responseDto = new ProblemResponseDto(id, "A", "Watermelon", 4L, 800, List.of("math"));

        when(problemRepository.findById(id)).thenReturn(Optional.of(problem));
        when(problemMapper.toDto(problem)).thenReturn(responseDto);

        ProblemResponseDto actual = problemService.getProblemById(id);

        assertNotNull(actual);
        assertEquals(id, actual.id());
    }

    @Test
    void testGetAllProblems() {
        Problem problem = Problem.builder().id(10L).build();
        ProblemResponseDto responseDto = new ProblemResponseDto(10L, "A", "Watermelon", 4L, 800, List.of("math"));

        when(problemRepository.findAll()).thenReturn(List.of(problem));
        when(problemMapper.toDto(problem)).thenReturn(responseDto);

        List<ProblemResponseDto> results = problemService.getAllProblems();

        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).id());
    }

    @Test
    void testDeleteProblem() {
        Long id = 10L;
        Problem problem = Problem.builder().id(id).build();

        when(problemRepository.findById(id)).thenReturn(Optional.of(problem));

        problemService.deleteProblem(id);

        verify(problemRepository).delete(problem);
    }
}
