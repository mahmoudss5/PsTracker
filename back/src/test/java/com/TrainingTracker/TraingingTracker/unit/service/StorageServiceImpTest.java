package com.TrainingTracker.TraingingTracker.unit.service;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Material.ImpMaterialService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.storage.storageServiceImp;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Storage.StorageUploadResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Material;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.MaterialRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageServiceImpTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private ImpMaterialService materialService;

    @Mock
    private PresignedPutObjectRequest presignedPut;

    @Mock
    private PresignedGetObjectRequest presignedGet;

    private storageServiceImp storageService;

    @BeforeEach
    void setUp() {
        storageService = new storageServiceImp(s3Client, s3Presigner, materialRepository, materialService);
        ReflectionTestUtils.setField(storageService, "bucketName", "test-bucket");
    }

    @Test
    void uploadGeneratesTeamScopedKeyAndFifteenMinuteUrl() throws Exception {
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedPut);
        when(presignedPut.url()).thenReturn(URI.create("https://s3.test/upload").toURL());

        StorageUploadResponse result = storageService.uploadFile(3L, "notes.pdf", "application/pdf", "123");

        ArgumentCaptor<PutObjectPresignRequest> request = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(s3Presigner).presignPutObject(request.capture());
        assertEquals("https://s3.test/upload", result.uploadUrl());
        assertTrue(result.s3Key().matches("teams/3/[0-9a-f-]{36}-notes\\.pdf"));
        assertEquals(Duration.ofMinutes(15), request.getValue().signatureDuration());
        assertEquals("test-bucket", request.getValue().putObjectRequest().bucket());
        assertEquals(result.s3Key(), request.getValue().putObjectRequest().key());
        assertEquals("application/pdf", request.getValue().putObjectRequest().contentType());
        assertEquals(123L, request.getValue().putObjectRequest().contentLength());
    }

    @Test
    void uploadRejectsInvalidFileSizeBeforePresigning() {
        assertThrows(NumberFormatException.class,
                () -> storageService.uploadFile(3L, "notes.pdf", "application/pdf", "not-a-number"));

        verify(s3Presigner, never()).presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    void getFileUrlRejectsUnknownMaterial() {
        when(materialRepository.findByS3Key("missing")).thenReturn(Optional.empty());

        AppException error = assertThrows(AppException.class, () -> storageService.getFileUrl("missing"));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
        verify(s3Presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void getFileUrlPreservesMaterialTypeAndDisplaysInline() throws Exception {
        when(materialRepository.findByS3Key("key"))
                .thenReturn(Optional.of(Material.builder().fileType("application/pdf").build()));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedGet);
        when(presignedGet.url()).thenReturn(URI.create("https://s3.test/view").toURL());

        assertEquals("https://s3.test/view", storageService.getFileUrl("key"));

        ArgumentCaptor<GetObjectPresignRequest> request = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(s3Presigner).presignGetObject(request.capture());
        assertEquals(Duration.ofMinutes(30), request.getValue().signatureDuration());
        assertEquals("test-bucket", request.getValue().getObjectRequest().bucket());
        assertEquals("key", request.getValue().getObjectRequest().key());
        assertEquals("inline", request.getValue().getObjectRequest().responseContentDisposition());
        assertEquals("application/pdf", request.getValue().getObjectRequest().responseContentType());
    }

    @Test
    void deleteRemovesMaterialBeforeS3Object() {
        storageService.deleteFile("key");

        ArgumentCaptor<DeleteObjectRequest> request = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        InOrder order = inOrder(materialService, s3Client);
        order.verify(materialService).deleteByS3Key("key");
        order.verify(s3Client).deleteObject(request.capture());
        assertEquals("test-bucket", request.getValue().bucket());
        assertEquals("key", request.getValue().key());
    }

    @Test
    void deleteDoesNotTouchS3WhenMaterialDeletionFails() {
        org.mockito.Mockito.doThrow(new AppException("missing", HttpStatus.NOT_FOUND))
                .when(materialService).deleteByS3Key("missing");

        assertThrows(AppException.class, () -> storageService.deleteFile("missing"));

        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void downloadUsesAttachmentDispositionAndFifteenMinuteUrl() throws Exception {
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedGet);
        when(presignedGet.url()).thenReturn(URI.create("https://s3.test/download").toURL());

        assertEquals("https://s3.test/download", storageService.downloadFile("key"));

        ArgumentCaptor<GetObjectPresignRequest> request = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(s3Presigner).presignGetObject(request.capture());
        assertEquals(Duration.ofMinutes(15), request.getValue().signatureDuration());
        assertEquals("attachment", request.getValue().getObjectRequest().responseContentDisposition());
        assertEquals("key", request.getValue().getObjectRequest().key());
    }
}
