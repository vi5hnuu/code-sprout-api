package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.configuration.AwsS3Properties;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class S3StorageService {
    private static final Logger logger = LoggerFactory.getLogger(S3StorageService.class);

    private final AwsS3Properties awsS3Properties;
    private final S3Client primaryS3Client;
    private final S3Client secondaryS3Client;

    public S3StorageService(
            @Qualifier("primaryS3Client") S3Client primaryS3Client,
            @Qualifier("secondaryS3Client") S3Client secondaryS3Client,
            AwsS3Properties awsS3Properties
    ) {
        this.primaryS3Client = primaryS3Client;
        this.secondaryS3Client = secondaryS3Client;
        this.awsS3Properties=awsS3Properties;
    }

    public String uploadFile(File file, @NotNull String key) throws IOException {
        try {
            // Perform the S3 upload
            PutObjectResponse response = primaryS3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(awsS3Properties.getPrimary().getBucketName())
                            .key(key) // S3 key (file name)
                            .build(),
                    RequestBody.fromFile(file) // File data
            );

            // Check if upload was successful
            if (response != null && response.sdkHttpResponse().isSuccessful()) {
                logger.info("File uploaded successfully with key: {}", key);
                return "File uploaded successfully";
            } else {
                // Handle failure if response is not successful
                logger.error("Failed to upload file with key: {}", key);
                throw new Exception("File upload failed");
            }

        } catch (S3Exception e) {
            // Catch S3-specific exceptions
            logger.error("S3 Exception occurred while uploading file: " + e.awsErrorDetails().errorMessage());
            throw new IOException("S3 upload failed: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            logger.error("Unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("Unexpected error during file upload", e);
        }
    }



    public String uploadedFilePath(String fileKey){
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                secondaryS3Client,
                awsS3Properties.getSecondary().getRegion(),
                fileKey);
    }
}
