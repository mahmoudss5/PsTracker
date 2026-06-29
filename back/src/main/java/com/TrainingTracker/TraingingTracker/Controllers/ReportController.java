package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ReportService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Create report")
    @PostMapping
    public ResponseEntity<ReportResponseDto> createReport(@RequestBody ReportCreateDto dto) {
        return ResponseEntity.ok(reportService.createReport(dto));
    }

    @Operation(summary = "Create report for current user")
    @PostMapping("/me")
    public ResponseEntity<ReportResponseDto> createCurrentUserReport(@RequestBody ReportUpdateDto dto) {
        return ResponseEntity.ok(reportService.createCurrentUserReport(dto));
    }

    @Operation(summary = "Get all reports")
    @GetMapping
    public ResponseEntity<List<ReportResponseDto>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @Operation(summary = "Get current user reports")
    @GetMapping("/me")
    public ResponseEntity<List<ReportResponseDto>> getCurrentUserReports() {
        return ResponseEntity.ok(reportService.getCurrentUserReports());
    }

    @Operation(summary = "Get reports by user id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReportResponseDto>> getReportsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getReportsByUserId(userId));
    }

    @Operation(summary = "Get report by id")
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDto> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @Operation(summary = "Update report")
    @PutMapping("/{id}")
    public ResponseEntity<ReportResponseDto> updateReport(
            @PathVariable Long id,
            @RequestBody ReportUpdateDto dto
    ) {
        return ResponseEntity.ok(reportService.updateReport(id, dto));
    }

    @Operation(summary = "Delete report")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
