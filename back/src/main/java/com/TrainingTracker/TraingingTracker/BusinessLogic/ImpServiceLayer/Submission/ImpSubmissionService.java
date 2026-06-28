package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Submission;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ProblemService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.SubmissionService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Submission.SubmissionUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Submission;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.SubmissionRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpSubmissionService implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserService userService;
    private final ProblemService problemService;
    private final SubmissionMapper submissionMapper;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "userSubmissions", key = "#dto.userId"),
        @CacheEvict(value = "solvedProblemsToday", key = "#dto.userId"),
        @CacheEvict(value = "allTrainees", allEntries = true)
    })
    public SubmissionResponseDto createSubmission(SubmissionCreateDto dto) {
        User user = userService.getUserById(dto.userId());
        Problem problem = problemService.getProblemEntityById(dto.problemId());
        Submission submission = submissionMapper.toEntity(dto, user, problem);
        return submissionMapper.toDto(submissionRepository.save(submission));
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "userSubmissions", allEntries = true),
        @CacheEvict(value = "solvedProblemsToday", allEntries = true),
        @CacheEvict(value = "allTrainees", allEntries = true)
    })
    public SubmissionResponseDto createCurrentUserSubmission(SubmissionCreateDto dto) {
        Long currentUserId = SecuiryUserUtil.getCurrntUserId();
        SubmissionCreateDto currentUserDto = new SubmissionCreateDto(
                currentUserId,
                dto.problemId(),
                dto.verdict(),
                dto.timeConsumedMs(),
                dto.memoryConsumedBytes()
        );
        return createSubmission(currentUserDto);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "submissions", key = "#id"),
        @CacheEvict(value = "userSubmissions", allEntries = true),
        @CacheEvict(value = "solvedProblemsToday", allEntries = true),
        @CacheEvict(value = "allTrainees", allEntries = true),
        @CacheEvict(value = "allSubmissionsCount", allEntries = true),
        @CacheEvict(value = "allAcceptedSubmissionsCount", allEntries = true),
        @CacheEvict(value = "acceptanceRate", allEntries = true),
        @CacheEvict(value = "timeLimitExceededCount", allEntries = true),
        @CacheEvict(value = "wrongAnswerCount", allEntries = true),
        @CacheEvict(value = "runTimeErrorCount", allEntries = true)
    })
    public SubmissionResponseDto updateSubmission(Long id, SubmissionUpdateDto dto) {
        Submission submission = getSubmissionEntityById(id);
        submissionMapper.updateEntity(submission, dto);
        return submissionMapper.toDto(submissionRepository.save(submission));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "submissions", key = "#id")
    public SubmissionResponseDto getSubmissionById(Long id) {
        return submissionMapper.toDto(getSubmissionEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponseDto> getAllSubmissions() {
        return submissionRepository.findAll()
                .stream()
                .map(submissionMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userSubmissions", key = "#userId")
    public List<SubmissionResponseDto> getSubmissionsByUserId(Long userId) {
        userService.getUserById(userId);
        return submissionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(submissionMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponseDto> getCurrentUserSubmissions() {
        return getSubmissionsByUserId(SecuiryUserUtil.getCurrntUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponseDto> getSubmissionsByProblemId(Long problemId) {
        problemService.getProblemEntityById(problemId);
        return submissionRepository.findByProblemIdOrderByCreatedAtDesc(problemId)
                .stream()
                .map(submissionMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "submissions", key = "#id"),
        @CacheEvict(value = "userSubmissions", allEntries = true),
        @CacheEvict(value = "solvedProblemsToday", allEntries = true),
        @CacheEvict(value = "allTrainees", allEntries = true)
    })
    public void deleteSubmission(Long id) {
        Submission submission = getSubmissionEntityById(id);
        submissionRepository.delete(submission);
    }

    @Override
    @Cacheable(value = "solvedProblemsToday", key = "#userId")
    public List<SubmissionResponseDto> getSolvedProblemsToday(Long userId) {

        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

        return submissionRepository
                .findAcceptedSubmissionsByUserAndDateRange(
                        userId,
                        startOfDay,
                        startOfNextDay
                )
                .stream()
                .map(submissionMapper::toDto)
                .toList();
    }



    @Override
    @Cacheable(value = "solvedProblemsTodayCount", key = "#userId")
    public Long countSolvedProblemsToday(Long userId) {
        return submissionRepository.countSolvedProblemsByUserId(userId);
    }

    @Override
    @Cacheable(value = "checkIfSubmissionExists", key = "#submissionId")
    public boolean checkIfSubmissionExists(Long submissionId) {

        return submissionRepository.existsById(submissionId);
    }

    @Override
    @Cacheable(value = "allSubmissionsCount", key = "#userId")
    public int getAllSubmissionsCount() {
        Long userId= SecuiryUserUtil.getCurrntUserId();
        User user = userService.getUserById(userId);
        int count = user.getSubmissions().size();
        return count;
    }

    @Override
    @Cacheable(value = "allAcceptedSubmissionsCount", key = "#userId")
    public int getAllAcceptedSubmissionsCount() {
        Long userId= SecuiryUserUtil.getCurrntUserId();
        User user = userService.getUserById(userId);
        int count=user.getSubmissions().stream()
                .filter(submission -> submission.getVerdict().equals("Accepted")
                        || submission.getVerdict().equals("Ok"))
                .toList()
                .size();
        return count;
    }

    @Override
    @Cacheable(value = "acceptanceRate", key = "#userId")
    public double getAcceptanceRate() {
        Long userId= SecuiryUserUtil.getCurrntUserId();
        User user = userService.getUserById(userId);
        int count=user.getSubmissions().size();
        int AcCount=user.getSubmissions().stream()
                .filter(submission -> submission.getVerdict().equals("Accepted")
                        || submission.getVerdict().equals("Ok"))
                .toList()
                .size();
        if (count!=0){
         return (double)AcCount/count*100;
        }
        return 0;
    }



    @Override
    @Cacheable(value = "timeLimitExceededCount", key = "#userId")
    public int getTimeLimitExceededCount() {
        Long userId= SecuiryUserUtil.getCurrntUserId();
        User user = userService.getUserById(userId);
        int count=user.getSubmissions().stream()
                .filter(submission -> submission.getVerdict().equals("Time Limit Exceeded"))
                .toList()
                .size();
        return count;
    }

    @Override
    @Cacheable(value = "wrongAnswerCount", key = "#userId")
    public int getWrongAnswerCount() {
        Long userId= SecuiryUserUtil.getCurrntUserId();
        User user = userService.getUserById(userId);
        int count=user.getSubmissions().stream()
                .filter(submission -> submission.getVerdict().equals("Wrong Answer"))
                .toList()
                .size();
        return count;
    }

    @Override
    @Cacheable(value = "runTimeErrorCount", key = "#userId")
    public int getRunTimeErrorCount() {
        Long userId= SecuiryUserUtil.getCurrntUserId();
        User user = userService.getUserById(userId);
        int count=user.getSubmissions().stream()
                .filter(submission -> submission.getVerdict().equals("Runtime Error"))
                .toList()
                .size();
        return count;
    }

    private Submission getSubmissionEntityById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found with id: " + id));
    }
}
