package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ContestService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Contest.ContestResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserIdRequestDto;
import com.TrainingTracker.TraingingTracker.Security.Aop.isSame.User.SameUserOrUserCoach;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    @GetMapping("/me")
    public ResponseEntity<List<ContestResponseDto>> getCurrentUserContests() {
        return ResponseEntity.ok(contestService.getCurrentUserContests());
    }

    @PostMapping("/user")
    @SameUserOrUserCoach
    public ResponseEntity<List<ContestResponseDto>> getContestsByUserId(
            @Valid @RequestBody UserIdRequestDto request) {
        return ResponseEntity.ok(contestService.getContestsByUserId(request.userId()));
    }
}
