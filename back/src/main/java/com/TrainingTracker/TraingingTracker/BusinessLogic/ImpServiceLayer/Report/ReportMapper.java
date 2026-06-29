package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Report;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Report.ReportUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Report;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReportMapper {

    public Report toEntity(ReportCreateDto dto, User user) {
        return Report.builder()
                .user(user)
                .numberOfProblems(dto.numberOfProblems())
                .numberOfHints(dto.numberOfHints())
                .comment(dto.comment())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Report toEntity(ReportUpdateDto dto, User user) {
        return Report.builder()
                .user(user)
                .numberOfProblems(dto.numberOfProblems())
                .numberOfHints(dto.numberOfHints())
                .comment(dto.comment())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateEntity(Report report, ReportUpdateDto dto) {
        report.setNumberOfProblems(dto.numberOfProblems());
        report.setNumberOfHints(dto.numberOfHints());
        report.setComment(dto.comment());
    }

    public ReportResponseDto toDto(Report report) {
        User user = report.getUser();
        return new ReportResponseDto(
                report.getId(),
                user == null ? null : user.getId(),
                user == null ? null : user.getUsername(),
                report.getNumberOfProblems(),
                report.getNumberOfHints(),
                report.getComment(),
                report.getCreatedAt()
        );
    }
}
