package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ContestService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Contest.ContestResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ContestResponseDto>> getContestsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(contestService.getContestsByUserId(userId));
    }
}
