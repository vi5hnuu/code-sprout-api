package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.configuration.AwsS3Properties;
import com.vi5hnu.codesprout.configuration.CacheConfig;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

@Service
public class S3StorageService {
    private static final Logger logger = LoggerFactory.getLogger(S3StorageService.class);

    private static final Duration PRESIGNED_URL_EXPIRY = Duration.ofHours(1);

    private final AwsS3Properties awsS3Properties;
    private final S3Client primaryS3Client;
    private final S3Client secondaryS3Client;
    private final S3Presigner primaryS3Presigner;
    private final S3Presigner secondaryS3Presigner;

    public S3StorageService(
            @Qualifier("primaryS3Client")     S3Client primaryS3Client,
            @Qualifier("secondaryS3Client")   S3Client secondaryS3Client,
            @Qualifier("primaryS3Presigner")  S3Presigner primaryS3Presigner,
            @Qualifier("secondaryS3Presigner") S3Presigner secondaryS3Presigner,
            AwsS3Properties awsS3Properties
    ) {
        this.primaryS3Client      = primaryS3Client;
        this.secondaryS3Client    = secondaryS3Client;
        this.primaryS3Presigner   = primaryS3Presigner;
        this.secondaryS3Presigner = secondaryS3Presigner;
        this.awsS3Properties      = awsS3Properties;
    }

    public Boolean deleteObject(@NotNull String key) {
        try {
            final var deleteResponse=primaryS3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(awsS3Properties.getPrimary().getBucketName())
                    .key(key)
                    .build());
            return true;
        } catch (S3Exception e) {
            // log the exception if needed
            System.err.println("Failed to delete S3 object: " + e.awsErrorDetails().errorMessage());
            return false;
        }
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



    /**
     * Downloads the content of an S3 object identified by its full HTTPS URL.
     * Extracts the key from the URL and downloads from the primary bucket using the primary client.
     */
    public String getObjectContent(String fileUrl) throws IOException {
        // Extract key: everything after ".amazonaws.com/"
        int idx = fileUrl.indexOf(".amazonaws.com/");
        if (idx == -1) throw new IOException("Invalid S3 URL: " + fileUrl);
        String key = fileUrl.substring(idx + ".amazonaws.com/".length());

        try {
            ResponseBytes<GetObjectResponse> bytes = primaryS3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(awsS3Properties.getPrimary().getBucketName())
                            .key(key)
                            .build()
            );
            return bytes.asUtf8String();
        } catch (S3Exception e) {
            logger.error("Failed to get S3 object key={}: {}", key, e.awsErrorDetails().errorMessage());
            throw new IOException("Failed to get S3 object: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Returns a presigned GET URL for the given S3 file URL.
     * Result is cached for 50 min (URL itself is valid for 1 hour),
     * giving a 10-min threshold before expiry — clients never receive a stale URL.
     */
    @Cacheable(value = CacheConfig.PRESIGNED_URLS_CACHE, key = "#fileUrl")
    public String getPresignedUrl(String fileUrl) {
        int idx = fileUrl.indexOf(".amazonaws.com/");
        if (idx == -1) throw new IllegalArgumentException("Invalid S3 URL: " + fileUrl);
        String key = fileUrl.substring(idx + ".amazonaws.com/".length());

        // Always use the primary bucket — uploadFile() always uploads there.
        // uploadedFilePath() incorrectly generates URLs with the secondary bucket name,
        // so the bucket name in the stored URL cannot be trusted.
        PresignedGetObjectRequest presigned = primaryS3Presigner.presignGetObject(r -> r
                .signatureDuration(PRESIGNED_URL_EXPIRY)
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(awsS3Properties.getPrimary().getBucketName())
                        .key(key)
                        .build())
        );
        logger.info("Generated presigned URL for key={}", key);
        return presigned.url().toString();
    }

    public String uploadedFilePath(String fileKey){
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                awsS3Properties.getPrimary().getBucketName(),
                awsS3Properties.getPrimary().getRegion(),
                fileKey);
    }
}
