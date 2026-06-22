package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionUpdateDto;

import java.util.List;

public interface SubmissionService {
    SubmissionResponseDto createSubmission(SubmissionCreateDto dto);

    SubmissionResponseDto createCurrentUserSubmission(SubmissionCreateDto dto);

    SubmissionResponseDto updateSubmission(Long id, SubmissionUpdateDto dto);

    SubmissionResponseDto getSubmissionById(Long id);

    List<SubmissionResponseDto> getAllSubmissions();

    List<SubmissionResponseDto> getSubmissionsByUserId(Long userId);

    List<SubmissionResponseDto> getCurrentUserSubmissions();

    List<SubmissionResponseDto> getSubmissionsByProblemId(Long problemId);

    void deleteSubmission(Long id);

    List<SubmissionResponseDto> getSolvedProblemsToday(Long userId);
    Long countSolvedProblemsToday(Long userId);
    
    boolean checkIfSubmissionExists(Long submissionId);
}

