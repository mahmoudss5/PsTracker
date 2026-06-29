package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportUpdateDto;

import java.util.List;

public interface ReportService {
    ReportResponseDto createReport(ReportCreateDto dto);

    ReportResponseDto createCurrentUserReport(ReportUpdateDto dto);

    ReportResponseDto getReportById(Long id);

    List<ReportResponseDto> getAllReports();

    List<ReportResponseDto> getReportsByUserId(Long userId);

    List<ReportResponseDto> getCurrentUserReports();

    ReportResponseDto updateReport(Long id, ReportUpdateDto dto);

    void deleteReport(Long id);
}
