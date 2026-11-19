package com.TrainingTracker.TraingingTracker.unit.controller;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ReportService;
import com.TrainingTracker.TraingingTracker.Controllers.ReportController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportUpdateDto;
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
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private ReportResponseDto responseDto() {
        return new ReportResponseDto(1L, 10L, "John", 5L, 1L, "Practice complete",
                LocalDateTime.of(2025, 1, 10, 12, 0));
    }

    @Test
    void testCreateReportUsesCurrentUser() {
        ReportCreateDto request = new ReportCreateDto(99L, 5L, 1L, "Practice complete");
        ReportUpdateDto dto = new ReportUpdateDto(5L, 1L, "Practice complete");
        ReportResponseDto responseDto = responseDto();
        when(reportService.createCurrentUserReport(dto)).thenReturn(responseDto);

        ResponseEntity<ReportResponseDto> response = reportController.createReport(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(reportService).createCurrentUserReport(dto);
    }

    @Test
    void testCreateCurrentUserReport() {
        ReportUpdateDto dto = new ReportUpdateDto(5L, 1L, "Practice complete");
        ReportResponseDto responseDto = responseDto();
        when(reportService.createCurrentUserReport(dto)).thenReturn(responseDto);

        ResponseEntity<ReportResponseDto> response = reportController.createCurrentUserReport(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(reportService).createCurrentUserReport(dto);
    }

    @Test
    void testGetAllReports() {
        List<ReportResponseDto> dtoList = List.of(responseDto());
        when(reportService.getAllReports()).thenReturn(dtoList);

        ResponseEntity<List<ReportResponseDto>> response = reportController.getAllReports();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(reportService).getAllReports();
    }

    @Test
    void testGetCurrentUserReports() {
        List<ReportResponseDto> dtoList = List.of(responseDto());
        when(reportService.getCurrentUserReports()).thenReturn(dtoList);

        ResponseEntity<List<ReportResponseDto>> response = reportController.getCurrentUserReports();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(reportService).getCurrentUserReports();
    }

    @Test
    void testGetReportsByUserId() {
        Long userId = 10L;
        UserIdRequestDto request = new UserIdRequestDto(userId);
        List<ReportResponseDto> dtoList = List.of(responseDto());
        when(reportService.getReportsByUserId(userId)).thenReturn(dtoList);

        ResponseEntity<List<ReportResponseDto>> response = reportController.getReportsByUserId(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(reportService).getReportsByUserId(userId);
    }

    @Test
    void testGetReportById() {
        Long reportId = 1L;
        ReportResponseDto responseDto = responseDto();
        when(reportService.getReportById(reportId)).thenReturn(responseDto);

        ResponseEntity<ReportResponseDto> response = reportController.getReportById(reportId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(reportService).getReportById(reportId);
    }

    @Test
    void testUpdateReport() {
        Long reportId = 1L;
        ReportUpdateDto dto = new ReportUpdateDto(6L, 2L, "Updated practice report");
        ReportResponseDto responseDto = new ReportResponseDto(reportId, 10L, "John", 6L, 2L,
                "Updated practice report", LocalDateTime.of(2025, 1, 10, 12, 0));
        when(reportService.updateReport(reportId, dto)).thenReturn(responseDto);

        ResponseEntity<ReportResponseDto> response = reportController.updateReport(reportId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(reportService).updateReport(reportId, dto);
    }

    @Test
    void testDeleteReport() {
        Long reportId = 1L;

        ResponseEntity<Void> response = reportController.deleteReport(reportId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(reportService).deleteReport(reportId);
    }
}
