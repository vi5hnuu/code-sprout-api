package com.vi5hnu.codesprout.services;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;

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

    public File contentToFile(@NotNull String content,@NotNull String fileName) throws IOException {
        File file=new File(fileName);
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        try(FileOutputStream fos=new FileOutputStream(file)){
            fos.write(bytes);
            return file;
        }catch (IOException e){
            log.error("Error converting content to file",e);
            throw e;
        }
    }
}
