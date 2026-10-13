package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Storage.StorageUploadResponse;

public interface storageService {
    public StorageUploadResponse uploadFile(Long  teamId, String fileName, String fileType, String fileSize);
    public String getFileUrl(String s3Key);
    public void deleteFile(String s3Key);
    public String downloadFile(String s3Key);

    

}
