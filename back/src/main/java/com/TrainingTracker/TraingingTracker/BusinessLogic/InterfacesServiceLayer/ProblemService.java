package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;

import java.util.List;

public interface ProblemService {
    Problem getProblemEntityById(Long id);

    ProblemResponseDto createProblem(ProblemCreateDto dto);

    ProblemResponseDto updateProblem(Long id, ProblemUpdateDto dto);

    ProblemResponseDto getProblemById(Long id);

    List<ProblemResponseDto> getAllProblems();

    void deleteProblem(Long id);
}
