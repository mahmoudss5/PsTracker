package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.storageService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Storage.StorageUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final storageService storageService;

    @Operation(summary = "Generate a presigned URL for uploading a file")
    @PostMapping("/upload-url")
    public ResponseEntity<StorageUploadResponse> generateUploadUrl(
            @RequestParam Long teamId,
            @RequestParam String fileName,
            @RequestParam String fileType,
            @RequestParam String fileSize) {

        return ResponseEntity.ok(storageService.uploadFile(teamId, fileName, fileType, fileSize));
    }

    @Operation(summary = "Generate a presigned URL for viewing a file inline")
    @GetMapping("/view-url")
    public ResponseEntity<Map<String, String>> generateViewUrl(@RequestParam String s3Key) {
        String viewUrl = storageService.getFileUrl(s3Key);
        return ResponseEntity.ok(Map.of("viewUrl", viewUrl));
    }

    @Operation(summary = "Generate a presigned URL for downloading a file")
    @GetMapping("/download-url")
    public ResponseEntity<Map<String, String>> generateDownloadUrl(@RequestParam String s3Key) {
        String downloadUrl = storageService.downloadFile(s3Key);
        return ResponseEntity.ok(Map.of("downloadUrl", downloadUrl));
    }

    @Operation(summary = "Delete a file from storage")
    @DeleteMapping
    public ResponseEntity<Void> deleteFile(@RequestParam String s3Key) {
        storageService.deleteFile(s3Key);
        return ResponseEntity.noContent().build();
    }
}
