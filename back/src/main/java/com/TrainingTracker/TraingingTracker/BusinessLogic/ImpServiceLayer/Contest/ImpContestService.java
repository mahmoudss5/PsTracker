package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Contest;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ContestService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Contest.ContestResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ContestRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImpContestService implements ContestService {

    private final ContestRepository contestRepository;
    private final ContestMapper contestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ContestResponseDto> getContestsByUserId(Long userId) {
        return contestRepository.findByUserIdOrderByRatingUpdateTimeAsc(userId)
                .stream()
                .map(contestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContestResponseDto> getCurrentUserContests() {
        Long currentUserId = SecuiryUserUtil.getCurrntUserId();
        return getContestsByUserId(currentUserId);
    }
}
