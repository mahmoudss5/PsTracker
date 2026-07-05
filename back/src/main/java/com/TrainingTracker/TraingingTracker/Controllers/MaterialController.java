package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.MaterialService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "Get materials for a team")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MaterialResponseDto>> getTeamMaterials(@PathVariable Long teamId) {
        return ResponseEntity.ok(materialService.getTeamMaterials(teamId));
    }

    @Operation(summary = "Add a material to a team")
    @PostMapping("/team/{teamId}")
    public ResponseEntity<MaterialResponseDto> addMaterial(
            @PathVariable Long teamId,
            @RequestBody MaterialCreateDto dto) {
        MaterialResponseDto response = materialService.addMaterial(teamId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
