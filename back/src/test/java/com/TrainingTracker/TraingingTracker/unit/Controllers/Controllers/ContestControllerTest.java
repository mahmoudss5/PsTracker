package com.TrainingTracker.TraingingTracker.unit.Controllers.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ContestService;
import com.TrainingTracker.TraingingTracker.Controllers.ContestController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Contest.ContestResponseDto;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContestControllerTest {

    @Mock
    private ContestService contestService;

    @InjectMocks
    private ContestController contestController;

    private ContestResponseDto responseDto() {
        return new ContestResponseDto(1L, 100L, "Round 100", 42, 1200, 1250,
                LocalDateTime.of(2025, 1, 10, 12, 0));
    }

    @Test
    void testGetCurrentUserContests() {
        List<ContestResponseDto> dtoList = List.of(responseDto());
        when(contestService.getCurrentUserContests()).thenReturn(dtoList);

        ResponseEntity<List<ContestResponseDto>> response = contestController.getCurrentUserContests();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(contestService).getCurrentUserContests();
    }

    @Test
    void testGetContestsByUserId() {
        Long userId = 10L;
        UserIdRequestDto request = new UserIdRequestDto(userId);
        List<ContestResponseDto> dtoList = List.of(responseDto());
        when(contestService.getContestsByUserId(userId)).thenReturn(dtoList);

        ResponseEntity<List<ContestResponseDto>> response = contestController.getContestsByUserId(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(contestService).getContestsByUserId(userId);
    }
}
