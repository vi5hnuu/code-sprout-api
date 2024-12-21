package com.vi5hnu.codesprout.services.problemArchive;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class S3StorageService {
    private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final S3Client s3Client;

    public PutObjectResponse uploadFile(MultipartFile multipartFile) throws IOException {
        final var file=multipartToFile(multipartFile);
        var response= s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(multipartFile.getOriginalFilename()) // S3 key (file name)
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

    private File multipartToFile(MultipartFile multipartFile) throws IOException {
        File file=new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
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
                bucketName,
                region,
                fileKey);
    }
}
