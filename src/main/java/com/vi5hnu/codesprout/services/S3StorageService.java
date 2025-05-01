package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.config.AwsS3Properties;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
public class S3StorageService {
    private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

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

    public PutObjectResponse uploadFile(MultipartFile multipartFile,@NotNull String key) throws IOException {
        final var file=multipartToFile(multipartFile,key);
        var response= primaryS3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(awsS3Properties.getPrimary().getBucketName())
                        .contentType(multipartFile.getContentType())
                        .key(key) // S3 key (file name)
                        .build(),
                RequestBody.fromFile(file) // File data
        );
        if(file.delete()){
            log.info("Deleted temporary file");
        }else {
            log.warn("File deletion failed");
        }
        return response;
    }

    private File multipartToFile(MultipartFile multipartFile,@NotNull String fileName) throws IOException {
        File file=new File(fileName);
        try(FileOutputStream fos=new FileOutputStream(file)){
            fos.write(multipartFile.getBytes());
            return file;
        }catch (IOException e){
            log.error("Error converting multipart file to file",e);
            throw e;
        }
    }

    public String uploadedFilePath(String fileKey){
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                secondaryS3Client,
                awsS3Properties.getSecondary().getRegion(),
                fileKey);
    }
}
