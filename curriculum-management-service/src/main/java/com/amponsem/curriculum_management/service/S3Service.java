package com.amponsem.curriculum_management.service;

import com.amponsem.curriculum_management.exception.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3Service implements FileStorageService {

    private final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            LOGGER.info("File successfully uploaded to S3: {}", fileName);
            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
        } catch (IOException ex) {
            LOGGER.error("File upload failed for file: {}. Error: {}", fileName, ex.getMessage(), ex);
            throw new FileUploadException("File upload failed due to I/O error for file: " + fileName);
        } catch (SdkClientException ex) {
            LOGGER.error("AWS S3 client error during file upload: {}", ex.getMessage(), ex);
            throw new FileUploadException("AWS S3 client error occurred during file upload for file: " + fileName);
        }
    }

}
