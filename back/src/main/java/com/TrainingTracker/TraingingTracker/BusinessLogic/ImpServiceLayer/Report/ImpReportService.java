package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Report;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ReportService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Report;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ReportRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpReportService implements ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final ReportMapper reportMapper;

    @Override
    @Transactional
    public ReportResponseDto createReport(ReportCreateDto dto) {
        User user = userService.getUserById(dto.userId());
        Report report = reportMapper.toEntity(dto, user);
        return reportMapper.toDto(reportRepository.save(report));
    }

    @Override
    @Transactional
    public ReportResponseDto createCurrentUserReport(ReportUpdateDto dto) {
        Long userId = SecuiryUserUtil.getCurrntUserId();
        User user = userService.getUserById(userId);
        Report report = reportMapper.toEntity(dto, user);
        return reportMapper.toDto(reportRepository.save(report));
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponseDto getReportById(Long id) {
        return reportMapper.toDto(getReportEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponseDto> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(reportMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponseDto> getReportsByUserId(Long userId) {
        userService.getUserById(userId);
        return reportRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(reportMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponseDto> getCurrentUserReports() {
        return getReportsByUserId(SecuiryUserUtil.getCurrntUserId());
    }

    @Override
    @Transactional
    public ReportResponseDto updateReport(Long id, ReportUpdateDto dto) {
        Report report = getReportEntityById(id);
        reportMapper.updateEntity(report, dto);
        return reportMapper.toDto(reportRepository.save(report));
    }

    @Override
    @Transactional
    public void deleteReport(Long id) {
        Report report = getReportEntityById(id);
        reportRepository.delete(report);
    }

    private Report getReportEntityById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }
}
