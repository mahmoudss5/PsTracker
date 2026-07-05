package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialResponseDto;

import java.util.List;

public interface MaterialService {
    List<MaterialResponseDto> getTeamMaterials(Long teamId);
    MaterialResponseDto addMaterial(Long teamId, MaterialCreateDto dto);
}
