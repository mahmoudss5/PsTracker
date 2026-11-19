package com.TrainingTracker.TraingingTracker.unit.service;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Contest.ContestMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Contest.ImpContestService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Contest.ContestResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Contest;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ContestRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpContestServiceTest {

    @Mock
    private ContestRepository contestRepository;

    private ImpContestService contestService;

    @BeforeEach
    void setUp() {
        contestService = new ImpContestService(contestRepository, new ContestMapper());
    }

    @Test
    void returnsContestsInRepositoryOrder() {
        Contest first = Contest.builder().id(1L).contestId(100L).contestName("Round 1").build();
        Contest second = Contest.builder().id(2L).contestId(200L).contestName("Round 2").build();
        when(contestRepository.findByUserIdOrderByRatingUpdateTimeAsc(7L))
                .thenReturn(List.of(first, second));

        List<ContestResponseDto> result = contestService.getContestsByUserId(7L);

        assertEquals(List.of("Round 1", "Round 2"), result.stream().map(ContestResponseDto::contestName).toList());
        assertEquals(100L, result.getFirst().contestId());
    }

    @Test
    void returnsEmptyListWhenUserHasNoContests() {
        when(contestRepository.findByUserIdOrderByRatingUpdateTimeAsc(7L)).thenReturn(List.of());

        assertTrue(contestService.getContestsByUserId(7L).isEmpty());
    }

    @Test
    void currentUserLookupUsesAuthenticatedUserId() {
        Contest contest = Contest.builder().id(1L).contestName("My round").build();
        when(contestRepository.findByUserIdOrderByRatingUpdateTimeAsc(7L)).thenReturn(List.of(contest));

        try (MockedStatic<SecuiryUserUtil> security = mockStatic(SecuiryUserUtil.class)) {
            security.when(SecuiryUserUtil::getCurrntUserId).thenReturn(7L);

            assertEquals("My round", contestService.getCurrentUserContests().getFirst().contestName());
        }

        verify(contestRepository).findByUserIdOrderByRatingUpdateTimeAsc(7L);
    }
}
