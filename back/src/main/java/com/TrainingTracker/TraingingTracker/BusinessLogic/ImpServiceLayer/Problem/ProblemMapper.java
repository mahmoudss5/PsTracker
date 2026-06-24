package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Problem;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.result.CodeforcesProblem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Problem.ProblemUpdateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Problem;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Tag;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProblemMapper {

    private final TagRepository tagRepository;

    public Problem toEntity(ProblemCreateDto dto) {
        return Problem.builder()
                .problemIndex(dto.problemIndex())
                .name(dto.name())
                .contestId(dto.contestId())
                .rating(dto.rating())
                .tags(resolveTags(dto.tags()))
                .build();
    }

    public void updateEntity(Problem problem, ProblemUpdateDto dto) {
        problem.setProblemIndex(dto.problemIndex());
        problem.setName(dto.name());
        problem.setContestId(dto.contestId());
        problem.setRating(dto.rating());
        problem.setTags(resolveTags(dto.tags()));
    }

    public ProblemResponseDto toDto(Problem problem) {
        return new ProblemResponseDto(
                problem.getId(),
                problem.getProblemIndex(),
                problem.getName(),
                problem.getContestId(),
                problem.getRating(),
                problem.getTags()
                        .stream()
                        .map(Tag::getTagName)
                        .toList()
        );
    }

    private List<Tag> resolveTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new ArrayList<>();
        }
        return tagNames.stream()
                .filter(tagName -> tagName != null && !tagName.isBlank())
                .map(String::trim)
                .distinct()
                .map(tagName -> tagRepository.findByTagName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().tagName(tagName).build())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Problem ToEntity(CodeforcesProblem problem){

        List<Tag>tags=problem.getTags().stream()
                .map(tagName->tagRepository.findByTagName(tagName)
                        .orElseGet(()->tagRepository.save(Tag.builder().tagName(tagName).build())))
                .toList();
        Problem problemEntity = Problem.builder()
                .name(problem.getName())
                .rating(Integer.parseInt(problem.getRating()))
                .tags(tags)
                .build();

        return problemEntity;
    }

}
