package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.storage;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.storageService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Material;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.MaterialRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Storage.StorageUploadResponse;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.AppException;
import java.time.Duration;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import java.util.UUID;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Material.ImpMaterialService;
@Service
@RequiredArgsConstructor
public class storageServiceImp implements storageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final MaterialRepository materialRepository; 
    private final  ImpMaterialService materialService;
    @Value("${aws.bucket-name}")
    private  String bucketName;

    @Override
    @WithSpan
    public StorageUploadResponse uploadFile(Long teamId, String fileName, String fileType, String fileSize) {
        String s3Key = generateS3Key(teamId, fileName);
        Span.current().setAttribute("TeamId", teamId);
        PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(
                        PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(s3Key)
                                .contentType(fileType)
                                .contentLength(Long.parseLong(fileSize))
                                .build())
                .build();

        String presignedUrl = s3Presigner.presignPutObject(request).url().toString();

        return new StorageUploadResponse(presignedUrl, s3Key);
    }

    @Override
    public String getFileUrl(String s3Key) {
       Material material = materialRepository.findByS3Key(s3Key)
            .orElseThrow(() -> new AppException("Material with s3Key " + s3Key + " not found", HttpStatus.NOT_FOUND));
  
       String mediaType = material.getFileType();
       

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .responseContentDisposition("inline")
        .responseContentType(mediaType)
        .build();

       
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(getObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignGetObject(request).url().toString();
        return presignedUrl;
    }

    @Override
    public void deleteFile(String s3Key) {
        materialService.deleteByS3Key(s3Key);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build());
        return;
    }

    @Override
    public String downloadFile(String s3Key) {
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(
                        GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(s3Key)
                                .responseContentDisposition("attachment")
                                .build())
                .build();
        String presignedUrl = s3Presigner.presignGetObject(request).url().toString();
        return presignedUrl;
    }

    private String generateS3Key(Long teamId, String fileName) {
        return "teams/" + teamId + "/" +UUID.randomUUID().toString()+"-"+ fileName;
    }
}
