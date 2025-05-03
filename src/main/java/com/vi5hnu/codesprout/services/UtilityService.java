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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class UtilityService {
    private static final Logger log = LoggerFactory.getLogger(UtilityService.class);

    public File multipartToFile(MultipartFile multipartFile,@NotNull String fileName) throws IOException {
        File file=new File(fileName);
        try(FileOutputStream fos=new FileOutputStream(file)){
            fos.write(multipartFile.getBytes());
            return file;
        }catch (IOException e){
            log.error("Error converting multipart file to file",e);
            throw e;
        }
    }
}
