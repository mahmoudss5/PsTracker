package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Problem;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.ProblemService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpProblemService implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemMapper problemMapper;

    @Override
    @Transactional(readOnly = true)
    public Problem getProblemEntityById(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "allProblems", allEntries = true)
    public ProblemResponseDto createProblem(ProblemCreateDto dto) {
        problemRepository.findByName(dto.name()).ifPresent(problem -> {
            throw new RuntimeException("Problem already exists with name: " + dto.name());
        });
        Problem problem = problemMapper.toEntity(dto);
        return problemMapper.toDto(problemRepository.save(problem));
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "problems", key = "#id"),
        @CacheEvict(value = "allProblems", allEntries = true)
    })
    public ProblemResponseDto updateProblem(Long id, ProblemUpdateDto dto) {
        Problem problem = getProblemEntityById(id);
        problemMapper.updateEntity(problem, dto);
        return problemMapper.toDto(problemRepository.save(problem));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "problems", key = "#id")
    public ProblemResponseDto getProblemById(Long id) {
        return problemMapper.toDto(getProblemEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allProblems")
    public List<ProblemResponseDto> getAllProblems() {
        return problemRepository.findAll()
                .stream()
                .map(problemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "problems", key = "#id"),
        @CacheEvict(value = "allProblems", allEntries = true)
    })
    public void deleteProblem(Long id) {
        Problem problem = getProblemEntityById(id);
        problemRepository.delete(problem);
    }
}
