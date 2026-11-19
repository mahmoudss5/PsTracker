package com.TrainingTracker.TraingingTracker.unit.service;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Material.ImpMaterialService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Material.MaterialResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Material;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.MaterialRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpMaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private ImpMaterialService materialService;

    @Test
    void returnsTeamMaterialsInRepositoryOrder() {
        Material newest = Material.builder().id(2L).title("Newest").s3Key("new").build();
        Material oldest = Material.builder().id(1L).title("Oldest").s3Key("old").build();
        when(materialRepository.findByTeamIdOrderByCreatedAtDesc(3L)).thenReturn(List.of(newest, oldest));

        List<MaterialResponseDto> result = materialService.getTeamMaterials(3L);

        assertEquals(List.of("Newest", "Oldest"), result.stream().map(MaterialResponseDto::title).toList());
    }

    @Test
    void returnsEmptyListWhenTeamHasNoMaterials() {
        when(materialRepository.findByTeamIdOrderByCreatedAtDesc(3L)).thenReturn(List.of());

        assertTrue(materialService.getTeamMaterials(3L).isEmpty());
    }

    @Test
    void addsMaterialToRequestedTeam() {
        Team team = Team.builder().id(3L).build();
        MaterialCreateDto dto = new MaterialCreateDto("application/pdf", "Notes", "123", "teams/3/notes.pdf");
        when(teamRepository.findById(3L)).thenReturn(Optional.of(team));
        when(materialRepository.save(any(Material.class))).thenAnswer(invocation -> {
            Material saved = invocation.getArgument(0);
            saved.setId(9L);
            return saved;
        });

        MaterialResponseDto result = materialService.addMaterial(3L, dto);

        ArgumentCaptor<Material> material = ArgumentCaptor.forClass(Material.class);
        verify(materialRepository).save(material.capture());
        assertSame(team, material.getValue().getTeam());
        assertEquals("application/pdf", material.getValue().getFileType());
        assertEquals("123", material.getValue().getFileSize());
        assertEquals(9L, result.id());
        assertEquals("Notes", result.title());
    }

    @Test
    void rejectsMaterialForUnknownTeam() {
        when(teamRepository.findById(3L)).thenReturn(Optional.empty());

        AppException error = assertThrows(AppException.class,
                () -> materialService.addMaterial(3L, new MaterialCreateDto("pdf", "Notes", "123", "key")));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
        verify(materialRepository, never()).save(any());
    }

    @Test
    void deletesOnlyMaterialBelongingToRequestedTeam() {
        when(materialRepository.findByS3KeyAndTeamId("key", 3L))
                .thenReturn(Optional.of(Material.builder().id(9L).build()));

        materialService.deleteMaterial(3L, "key");

        verify(materialRepository).deleteById(9L);
    }

    @Test
    void rejectsDeletionWhenMaterialIsNotInTeam() {
        when(materialRepository.findByS3KeyAndTeamId("key", 3L)).thenReturn(Optional.empty());

        AppException error = assertThrows(AppException.class, () -> materialService.deleteMaterial(3L, "key"));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
        verify(materialRepository, never()).deleteById(any());
    }

    @Test
    void deletesMaterialByS3Key() {
        when(materialRepository.findByS3Key("key"))
                .thenReturn(Optional.of(Material.builder().id(9L).build()));

        materialService.deleteByS3Key("key");

        verify(materialRepository).deleteById(9L);
    }

    @Test
    void rejectsDeletionForUnknownS3Key() {
        when(materialRepository.findByS3Key("missing")).thenReturn(Optional.empty());

        AppException error = assertThrows(AppException.class, () -> materialService.deleteByS3Key("missing"));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
        verify(materialRepository, never()).deleteById(any());
    }

    @Test
    void looksUpTeamIdForS3Key() {
        Material material = Material.builder().team(Team.builder().id(3L).build()).build();
        when(materialRepository.findByS3Key("key")).thenReturn(Optional.of(material));

        assertEquals(3L, materialService.getTeamIdByS3Key("key"));
    }

    @Test
    void returnsZeroWhenMaterialHasNoTeam() {
        when(materialRepository.findByS3Key("key")).thenReturn(Optional.of(Material.builder().build()));

        assertEquals(0L, materialService.getTeamIdByS3Key("key"));
    }

    @Test
    void rejectsTeamLookupForUnknownS3Key() {
        when(materialRepository.findByS3Key("missing")).thenReturn(Optional.empty());

        AppException error = assertThrows(AppException.class, () -> materialService.getTeamIdByS3Key("missing"));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
    }
}
