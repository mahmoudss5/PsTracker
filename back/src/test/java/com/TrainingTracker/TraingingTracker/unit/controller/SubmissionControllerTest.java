package com.TrainingTracker.TraingingTracker.unit.controller;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.SubmissionService;
import com.TrainingTracker.TraingingTracker.Controllers.SubmissionController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserIdRequestDto;
import java.time.LocalDateTime;
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
class SubmissionControllerTest {

    @Mock
    private SubmissionService submissionService;

    @InjectMocks
    private SubmissionController submissionController;

    private SubmissionResponseDto responseDto() {
        return new SubmissionResponseDto(1L, 10L, "John", 20L, "Shortest Path", "OK", 100, 1024,
                LocalDateTime.of(2025, 1, 10, 12, 0));
    }

    @Test
    void testCreateSubmission() {
        SubmissionCreateDto dto = new SubmissionCreateDto(10L, 20L, "OK", 100, 1024);
        SubmissionResponseDto responseDto = responseDto();
        when(submissionService.createCurrentUserSubmission(dto)).thenReturn(responseDto);

        ResponseEntity<SubmissionResponseDto> response = submissionController.createSubmission(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(submissionService).createCurrentUserSubmission(dto);
    }

    @Test
    void testCreateCurrentUserSubmission() {
        SubmissionCreateDto dto = new SubmissionCreateDto(10L, 20L, "OK", 100, 1024);
        SubmissionResponseDto responseDto = responseDto();
        when(submissionService.createCurrentUserSubmission(dto)).thenReturn(responseDto);

        ResponseEntity<SubmissionResponseDto> response = submissionController.createCurrentUserSubmission(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(submissionService).createCurrentUserSubmission(dto);
    }

    @Test
    void testGetAllSubmissions() {
        List<SubmissionResponseDto> dtoList = List.of(responseDto());
        when(submissionService.getAllSubmissions()).thenReturn(dtoList);

        ResponseEntity<List<SubmissionResponseDto>> response = submissionController.getAllSubmissions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(submissionService).getAllSubmissions();
    }

    @Test
    void testGetCurrentUserSubmissions() {
        List<SubmissionResponseDto> dtoList = List.of(responseDto());
        when(submissionService.getCurrentUserSubmissions()).thenReturn(dtoList);

        ResponseEntity<List<SubmissionResponseDto>> response = submissionController.getCurrentUserSubmissions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(submissionService).getCurrentUserSubmissions();
    }

    @Test
    void testGetSubmissionsByUserId() {
        Long userId = 10L;
        UserIdRequestDto request = new UserIdRequestDto(userId);
        List<SubmissionResponseDto> dtoList = List.of(responseDto());
        when(submissionService.getSubmissionsByUserId(userId)).thenReturn(dtoList);

        ResponseEntity<List<SubmissionResponseDto>> response = submissionController.getSubmissionsByUserId(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(submissionService).getSubmissionsByUserId(userId);
    }

    @Test
    void testGetSubmissionsByProblemId() {
        Long problemId = 20L;
        List<SubmissionResponseDto> dtoList = List.of(responseDto());
        when(submissionService.getSubmissionsByProblemId(problemId)).thenReturn(dtoList);

        ResponseEntity<List<SubmissionResponseDto>> response = submissionController.getSubmissionsByProblemId(problemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(submissionService).getSubmissionsByProblemId(problemId);
    }

    @Test
    void testGetSubmissionById() {
        Long submissionId = 1L;
        SubmissionResponseDto responseDto = responseDto();
        when(submissionService.getSubmissionById(submissionId)).thenReturn(responseDto);

        ResponseEntity<SubmissionResponseDto> response = submissionController.getSubmissionById(submissionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(submissionService).getSubmissionById(submissionId);
    }

    @Test
    void testUpdateSubmission() {
        Long submissionId = 1L;
        SubmissionUpdateDto dto = new SubmissionUpdateDto("WRONG_ANSWER", 200, 2048);
        SubmissionResponseDto responseDto = new SubmissionResponseDto(submissionId, 10L, "John", 20L,
                "Shortest Path", "WRONG_ANSWER", 200, 2048, LocalDateTime.of(2025, 1, 10, 12, 0));
        when(submissionService.updateSubmission(submissionId, dto)).thenReturn(responseDto);

        ResponseEntity<SubmissionResponseDto> response = submissionController.updateSubmission(submissionId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(submissionService).updateSubmission(submissionId, dto);
    }

    @Test
    void testDeleteSubmission() {
        Long submissionId = 1L;

        ResponseEntity<Void> response = submissionController.deleteSubmission(submissionId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(submissionService).deleteSubmission(submissionId);
    }
}
