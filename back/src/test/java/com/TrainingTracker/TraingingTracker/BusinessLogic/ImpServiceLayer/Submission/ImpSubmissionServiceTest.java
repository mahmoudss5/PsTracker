package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Submission;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ProblemService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.SubmissionRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpSubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProblemService problemService;

    @Mock
    private SubmissionMapper submissionMapper;

    @InjectMocks
    private ImpSubmissionService submissionService;

    @Test
    void testCreateSubmission() {
        SubmissionCreateDto createDto = new SubmissionCreateDto(1L, 2L, "OK", 100, 1024);
        User user = User.builder().id(1L).build();
        Problem problem = Problem.builder().id(2L).build();
        Submission submission = Submission.builder().id(10L).build();
        SubmissionResponseDto responseDto = new SubmissionResponseDto(
                10L, 1L, "user1", 2L, "Watermelon", "OK", 100, 1024, LocalDateTime.now()
        );

        when(userService.getUserById(1L)).thenReturn(user);
        when(problemService.getProblemEntityById(2L)).thenReturn(problem);
        when(submissionMapper.toEntity(createDto, user, problem)).thenReturn(submission);
        when(submissionRepository.save(submission)).thenReturn(submission);
        when(submissionMapper.toDto(submission)).thenReturn(responseDto);

        SubmissionResponseDto actual = submissionService.createSubmission(createDto);

        assertNotNull(actual);
        assertEquals(10L, actual.id());
        verify(submissionRepository).save(submission);
    }

    @Test
    void testCreateCurrentUserSubmission() {
        Long currentUserId = 42L;
        SubmissionCreateDto createDto = new SubmissionCreateDto(null, 2L, "OK", 100, 1024);
        
        User user = User.builder().id(currentUserId).build();
        Problem problem = Problem.builder().id(2L).build();
        Submission submission = Submission.builder().id(10L).build();
        SubmissionResponseDto responseDto = new SubmissionResponseDto(
                10L, currentUserId, "user1", 2L, "Watermelon", "OK", 100, 1024, LocalDateTime.now()
        );

        try (MockedStatic<SecuiryUserUtil> securityUtilMockedStatic = mockStatic(SecuiryUserUtil.class)) {
            securityUtilMockedStatic.when(SecuiryUserUtil::getCurrntUserId).thenReturn(currentUserId);
            when(userService.getUserById(currentUserId)).thenReturn(user);
            when(problemService.getProblemEntityById(2L)).thenReturn(problem);
            when(submissionMapper.toEntity(any(SubmissionCreateDto.class), eq(user), eq(problem))).thenReturn(submission);
            when(submissionRepository.save(submission)).thenReturn(submission);
            when(submissionMapper.toDto(submission)).thenReturn(responseDto);

            SubmissionResponseDto actual = submissionService.createCurrentUserSubmission(createDto);

            assertNotNull(actual);
            assertEquals(10L, actual.id());
        }
    }

    @Test
    void testUpdateSubmission() {
        Long id = 10L;
        SubmissionUpdateDto updateDto = new SubmissionUpdateDto("WRONG_ANSWER", 200, 2048);
        Submission submission = Submission.builder().id(id).build();
        SubmissionResponseDto responseDto = new SubmissionResponseDto(
                id, 1L, "user1", 2L, "Watermelon", "WRONG_ANSWER", 200, 2048, LocalDateTime.now()
        );

        when(submissionRepository.findById(id)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(submission)).thenReturn(submission);
        when(submissionMapper.toDto(submission)).thenReturn(responseDto);

        SubmissionResponseDto actual = submissionService.updateSubmission(id, updateDto);

        assertNotNull(actual);
        assertEquals(id, actual.id());
        verify(submissionMapper).updateEntity(submission, updateDto);
    }

    @Test
    void testGetSubmissionById_Success() {
        Long id = 10L;
        Submission submission = Submission.builder().id(id).build();
        SubmissionResponseDto responseDto = new SubmissionResponseDto(
                id, 1L, "user1", 2L, "Watermelon", "OK", 100, 1024, LocalDateTime.now()
        );

        when(submissionRepository.findById(id)).thenReturn(Optional.of(submission));
        when(submissionMapper.toDto(submission)).thenReturn(responseDto);

        SubmissionResponseDto actual = submissionService.getSubmissionById(id);

        assertNotNull(actual);
        assertEquals(id, actual.id());
    }

    @Test
    void testGetSubmissionById_NotFound() {
        Long id = 999L;
        when(submissionRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> submissionService.getSubmissionById(id));
        assertEquals("Submission not found with id: " + id, exception.getMessage());
    }

    @Test
    void testGetSubmissionsByUserId() {
        Long userId = 1L;
        Submission sub = Submission.builder().id(10L).build();
        SubmissionResponseDto responseDto = new SubmissionResponseDto(
                10L, userId, "user1", 2L, "Watermelon", "OK", 100, 1024, LocalDateTime.now()
        );

        when(submissionRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(sub));
        when(submissionMapper.toDto(sub)).thenReturn(responseDto);

        List<SubmissionResponseDto> results = submissionService.getSubmissionsByUserId(userId);

        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).id());
        verify(userService).getUserById(userId);
    }

    @Test
    void testGetSolvedProblemsToday() {
        Long userId = 1L;
        Submission sub = Submission.builder().id(10L).build();
        SubmissionResponseDto responseDto = new SubmissionResponseDto(
                10L, userId, "user1", 2L, "Watermelon", "OK", 100, 1024, LocalDateTime.now()
        );

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

        when(submissionRepository.findAcceptedSubmissionsByUserAndDateRange(userId, startOfDay, startOfNextDay))
                .thenReturn(List.of(sub));
        when(submissionMapper.toDto(sub)).thenReturn(responseDto);

        List<SubmissionResponseDto> results = submissionService.getSolvedProblemsToday(userId);

        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).id());
    }

    @Test
    void testCountSolvedProblemsToday() {
        Long userId = 1L;
        when(submissionRepository.countSolvedProblemsByUserId(userId)).thenReturn(5L);

        Long count = submissionService.countSolvedProblemsToday(userId);

        assertEquals(5L, count);
        verify(submissionRepository).countSolvedProblemsByUserId(userId);
    }

    @Test
    void testCheckIfSubmissionExists() {
        Long submissionId = 100L;
        when(submissionRepository.existsById(submissionId)).thenReturn(true);

        boolean exists = submissionService.checkIfSubmissionExists(submissionId);

        assertTrue(exists);
        verify(submissionRepository).existsById(submissionId);
    }
}
