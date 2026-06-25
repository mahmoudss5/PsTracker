package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.AnnouncmentService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncementResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncmentCreateDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcment-teams")
@RequiredArgsConstructor
@Slf4j
public class AnnouncmentTeamController {

    private final AnnouncmentService announcmentService;

    @Operation(summary = "Get all announcements for a specific team")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<AnnouncementResponseDto>> getAllForTeam(@PathVariable Long teamId) {
        log.info("Fetching announcements for team: {}", teamId);
        List<AnnouncementResponseDto> announcements = announcmentService.getAllAnnouncmentsForTeam(teamId);
        return ResponseEntity.ok(announcements);
    }

    @Operation(summary = "Send an announcement to a team")
    @PostMapping
    public ResponseEntity<Void> sendAnnouncementToTeam(@RequestBody AnnouncmentCreateDto announcementCreateDto) {
        log.info("Sending announcement to team: {}", announcementCreateDto.receiverId());
        // Force isTeamAnnouncment to true for safety
        AnnouncmentCreateDto dto = new AnnouncmentCreateDto(
                announcementCreateDto.type(),
                announcementCreateDto.content(),
                announcementCreateDto.senderId(),
                true,
                announcementCreateDto.receiverId()
        );
        announcmentService.sendAnnouncment(dto);
        return ResponseEntity.ok().build();
    }
}
