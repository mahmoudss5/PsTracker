package com.TrainingTracker.TraingingTracker.unit.service;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Report.ImpReportService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Report.ReportMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Report;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ReportRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    private ImpReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ImpReportService(reportRepository, userService, new ReportMapper());
    }

    @Test
    void createsReportForSpecifiedUser() {
        User user = User.builder().id(7L).username("trainee").build();
        ReportCreateDto dto = new ReportCreateDto(7L, 5L, 2L, "Good work");
        when(userService.getUserById(7L)).thenReturn(user);
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            report.setId(10L);
            return report;
        });

        ReportResponseDto result = reportService.createReport(dto);

        ArgumentCaptor<Report> saved = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(saved.capture());
        assertSame(user, saved.getValue().getUser());
        assertEquals(5L, saved.getValue().getNumberOfProblems());
        assertEquals(2L, saved.getValue().getNumberOfHints());
        assertNotNull(saved.getValue().getCreatedAt());
        assertEquals(10L, result.id());
        assertEquals("trainee", result.userName());
    }

    @Test
    void createsCurrentUserReportWithAuthenticatedUserId() {
        User user = User.builder().id(7L).username("trainee").build();
        when(userService.getUserById(7L)).thenReturn(user);
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecuiryUserUtil> security = mockStatic(SecuiryUserUtil.class)) {
            security.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);

            ReportResponseDto result = reportService.createCurrentUserReport(new ReportUpdateDto(4L, 1L, "Today"));

            assertEquals(7L, result.userId());
            assertEquals("Today", result.comment());
        }
    }

    @Test
    void getsReportById() {
        Report report = report(10L, 7L, "First");
        when(reportRepository.findById(10L)).thenReturn(Optional.of(report));

        ReportResponseDto result = reportService.getReportById(10L);

        assertEquals(10L, result.id());
        assertEquals("First", result.comment());
    }

    @Test
    void missingReportFailsLookup() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class, () -> reportService.getReportById(99L));

        assertEquals("Report not found with id: 99", error.getMessage());
    }

    @Test
    void listsAllReportsInRepositoryOrder() {
        when(reportRepository.findAll()).thenReturn(List.of(report(1L, 7L, "First"), report(2L, 8L, "Second")));

        List<ReportResponseDto> result = reportService.getAllReports();

        assertEquals(List.of("First", "Second"), result.stream().map(ReportResponseDto::comment).toList());
    }

    @Test
    void listsOnlyReportsForExistingUser() {
        when(userService.getUserById(7L)).thenReturn(User.builder().id(7L).build());
        when(reportRepository.findByUserIdOrderByCreatedAtDesc(7L))
                .thenReturn(List.of(report(2L, 7L, "Newest"), report(1L, 7L, "Oldest")));

        List<ReportResponseDto> result = reportService.getReportsByUserId(7L);

        assertEquals(List.of("Newest", "Oldest"), result.stream().map(ReportResponseDto::comment).toList());
        verify(userService).getUserById(7L);
    }

    @Test
    void unknownUserCannotQueryReports() {
        when(userService.getUserById(99L)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> reportService.getReportsByUserId(99L));

        verify(reportRepository, never()).findByUserIdOrderByCreatedAtDesc(any());
    }

    @Test
    void listsReportsForAuthenticatedUser() {
        when(userService.getUserById(7L)).thenReturn(User.builder().id(7L).build());
        when(reportRepository.findByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of(report(1L, 7L, "Mine")));

        try (MockedStatic<SecuiryUserUtil> security = mockStatic(SecuiryUserUtil.class)) {
            security.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);

            assertEquals("Mine", reportService.getCurrentUserReports().getFirst().comment());
        }
    }

    @Test
    void updatesReportAndKeepsItsOwnerAndCreationTime() {
        Report report = report(10L, 7L, "Old");
        LocalDateTime createdAt = report.getCreatedAt();
        when(reportRepository.findById(10L)).thenReturn(Optional.of(report));
        when(reportRepository.save(report)).thenReturn(report);

        ReportResponseDto result = reportService.updateReport(10L, new ReportUpdateDto(9L, 3L, "Updated"));

        assertEquals("Updated", result.comment());
        assertEquals(9L, result.numberOfProblems());
        assertEquals(3L, result.numberOfHints());
        assertEquals(7L, result.userId());
        assertEquals(createdAt, report.getCreatedAt());
        verify(reportRepository).save(report);
    }

    @Test
    void cannotUpdateMissingReport() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> reportService.updateReport(99L, new ReportUpdateDto(1L, 1L, "New")));

        verify(reportRepository, never()).save(any());
    }

    @Test
    void deletesExistingReport() {
        Report report = report(10L, 7L, "Old");
        when(reportRepository.findById(10L)).thenReturn(Optional.of(report));

        reportService.deleteReport(10L);

        verify(reportRepository).delete(report);
    }

    @Test
    void cannotDeleteMissingReport() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.deleteReport(99L));

        verify(reportRepository, never()).delete(any());
    }

    private Report report(Long id, Long userId, String comment) {
        return Report.builder().id(id).user(User.builder().id(userId).username("trainee").build())
                .numberOfProblems(3L).numberOfHints(1L).comment(comment)
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();
    }
}
