package com.TrainingTracker.TraingingTracker.unit.controller;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.MaterialService;
import com.TrainingTracker.TraingingTracker.Controllers.MaterialController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialResponseDto;
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
class MaterialControllerTest {

    @Mock
    private MaterialService materialService;

    @InjectMocks
    private MaterialController materialController;

    private MaterialResponseDto responseDto() {
        return new MaterialResponseDto(1L, "application/pdf", "Study Guide", null,
                "1024", "teams/10/guide.pdf", LocalDateTime.of(2025, 1, 10, 12, 0));
    }

    @Test
    void testGetTeamMaterials() {
        Long teamId = 10L;
        List<MaterialResponseDto> dtoList = List.of(responseDto());
        when(materialService.getTeamMaterials(teamId)).thenReturn(dtoList);

        ResponseEntity<List<MaterialResponseDto>> response = materialController.getTeamMaterials(teamId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(materialService).getTeamMaterials(teamId);
    }

    @Test
    void testAddMaterial() {
        Long teamId = 10L;
        MaterialCreateDto dto = new MaterialCreateDto("application/pdf", "Study Guide", "1024",
                "teams/10/guide.pdf");
        MaterialResponseDto responseDto = responseDto();
        when(materialService.addMaterial(teamId, dto)).thenReturn(responseDto);

        ResponseEntity<MaterialResponseDto> response = materialController.addMaterial(teamId, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(materialService).addMaterial(teamId, dto);
    }
}
