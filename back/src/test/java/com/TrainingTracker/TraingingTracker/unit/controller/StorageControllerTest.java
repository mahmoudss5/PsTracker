package com.TrainingTracker.TraingingTracker.unit.controller;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.storageService;
import com.TrainingTracker.TraingingTracker.Controllers.StorageController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Storage.StorageUploadResponse;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageControllerTest {

    @Mock
    private storageService storageService;

    @InjectMocks
    private StorageController storageController;

    @Test
    void testGenerateUploadUrl() {
        Long teamId = 10L;
        String fileName = "guide.pdf";
        String fileType = "application/pdf";
        String fileSize = "1024";
        StorageUploadResponse responseDto = new StorageUploadResponse("https://example.com/upload",
                "teams/10/guide.pdf");
        when(storageService.uploadFile(teamId, fileName, fileType, fileSize)).thenReturn(responseDto);

        ResponseEntity<StorageUploadResponse> response = storageController.generateUploadUrl(teamId, fileName, fileType, fileSize);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(storageService).uploadFile(teamId, fileName, fileType, fileSize);
    }

    @Test
    void testGenerateViewUrl() {
        String s3Key = "teams/10/guide.pdf";
        String url = "https://example.com/view";
        when(storageService.getFileUrl(s3Key)).thenReturn(url);

        ResponseEntity<Map<String, String>> response = storageController.generateViewUrl(s3Key);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("viewUrl", url), response.getBody());
        verify(storageService).getFileUrl(s3Key);
    }

    @Test
    void testGenerateDownloadUrl() {
        String s3Key = "teams/10/guide.pdf";
        String url = "https://example.com/download";
        when(storageService.downloadFile(s3Key)).thenReturn(url);

        ResponseEntity<Map<String, String>> response = storageController.generateDownloadUrl(s3Key);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("downloadUrl", url), response.getBody());
        verify(storageService).downloadFile(s3Key);
    }

    @Test
    void testDeleteFile() {
        String s3Key = "teams/10/guide.pdf";

        ResponseEntity<Void> response = storageController.deleteFile(s3Key);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(storageService).deleteFile(s3Key);
    }
}
