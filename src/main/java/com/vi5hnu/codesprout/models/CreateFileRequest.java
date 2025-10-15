package com.vi5hnu.codesprout.models;

import com.vi5hnu.codesprout.enums.FileAccess;
import com.vi5hnu.codesprout.enums.Visibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFileRequest {
    private String folderId;

    //this name will override file name in s3 bucket and hence the key
    private String name;//can be null in that case file name is used
    private Visibility visibility = Visibility.PRIVATE;
    private FileAccess access = FileAccess.PREMIUM;
    private VideoSource videoSource = new VideoSource();
}
