package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.AnnouncmentService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncementResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment.AnnouncmentCreateDto;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcment")
@RequiredArgsConstructor
@Slf4j
public class AnnouncmentController {

    private final AnnouncmentService announcmentService;


    @Operation(summary = "get all announcmnet for specif user")
    @GetMapping("/getAllForUser")
    public ResponseEntity<List<AnnouncementResponseDto>> getAll(){
           List<AnnouncementResponseDto> announcments = announcmentService
                   .getAllAnnouncmentsForUser(SecuiryUserUtil.getCurrntUserId());
           return ResponseEntity.ok(announcments);
    }
    @Operation(summary = "get all announcmnet for specif team")
    @GetMapping("/getAllForTeam")
    public ResponseEntity<List<AnnouncementResponseDto>>geAllForTeam(@RequestParam Long teamId){
        List<AnnouncementResponseDto> announcments=announcmentService.getAllAnnouncmentsForTeam(teamId);
        return ResponseEntity.ok(announcments);
    }

    @Operation(summary = "Send an announcement to a user or a team")
    @PostMapping("/sendAnnouncmnet")
    public void sendAnnouncment(@RequestBody AnnouncmentCreateDto announcmentCreateDto) {
        AnnouncmentCreateDto authenticatedSenderDto = new AnnouncmentCreateDto(
                announcmentCreateDto.type(),
                announcmentCreateDto.content(),
                SecuiryUserUtil.getCurrntUserId(),
                announcmentCreateDto.isTeamAnnouncment(),
                announcmentCreateDto.receiverId()
        );
        announcmentService.sendAnnouncment(authenticatedSenderDto);
    }


}
