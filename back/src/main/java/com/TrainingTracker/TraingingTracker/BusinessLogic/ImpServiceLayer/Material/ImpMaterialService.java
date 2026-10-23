package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Material;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.MaterialService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Material;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.MaterialRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import io.opentelemetry.instrumentation.annotations.WithSpan;

@Service
@RequiredArgsConstructor
public class ImpMaterialService implements MaterialService {

    private final MaterialRepository materialRepository;
    private final TeamRepository teamRepository;

    @Override
    @WithSpan
    public List<MaterialResponseDto> getTeamMaterials(Long teamId) {
        return materialRepository.findByTeamIdOrderByCreatedAtDesc(teamId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @WithSpan
    public MaterialResponseDto addMaterial(Long teamId, MaterialCreateDto dto) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException("Team not found", HttpStatus.NOT_FOUND));

        Material material = Material.builder()
                .s3Key(dto.s3Key())
                .fileType(dto.fileType())
                .title(dto.title())
                .fileSize(dto.fileSize())
                .team(team)
                .build();

        Material saved = materialRepository.save(material);
        return toDto(saved);
    }

    @Override
    @WithSpan
    public void deleteMaterial(Long teamId, String s3Key) {
        Material material = materialRepository.findByS3KeyAndTeamId(s3Key, teamId)
                .orElseThrow(() -> new AppException("material not found", HttpStatus.NOT_FOUND));
        materialRepository.deleteById(material.getId());
    }

    @Override
    @WithSpan
    public void deleteByS3Key(String s3Key) {
        Material material = materialRepository.findByS3Key(s3Key)
                .orElseThrow(() -> new AppException("Team not found", HttpStatus.NOT_FOUND));

        materialRepository.deleteById(material.getId());
    }

    @Override
    public Long getTeamIdByS3Key(String s3Key) {
        Material material=materialRepository.findByS3Key(s3Key)
                .orElseThrow(() -> new AppException("Material not found", HttpStatus.NOT_FOUND));
        if(material.getTeam()!=null){
            return material.getTeam().getId();
        }
        return 0L;
    }

    private MaterialResponseDto toDto(Material material) {
        return new MaterialResponseDto(
                material.getId(),
                material.getFileType(),
                material.getTitle(),
                material.getSubtitle(),
                material.getFileSize(),
                material.getS3Key(),
                material.getCreatedAt());
    }
}
