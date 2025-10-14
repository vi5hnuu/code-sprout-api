package com.vi5hnu.codesprout.models.folderStructure;

import com.vi5hnu.codesprout.entity.File;
import com.vi5hnu.codesprout.enums.FileAccess;
import com.vi5hnu.codesprout.enums.FileExtension;
import com.vi5hnu.codesprout.enums.Visibility;
import com.vi5hnu.codesprout.models.VideoSource;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtFile implements FSItemDto {
    private String id;
    private String ownerId;
    private String folderId;
    private String name;
    private FileExtension fileExtension;
    private String mimeType;
    private String s3Key;
    private FileAccess access;
    private VideoSource videoSource;
    private Long fileSize;//in bytes

    @Builder.Default()
    private Visibility visibility = Visibility.PRIVATE;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public static ExtFile fromFile(File file){
        final var extFile=ExtFile.builder()
                .id(file.getId())
                .name(file.getName())
                .ownerId(file.getOwnerId())
                .folderId(file.getFolderId())
                .fileExtension(file.getFileExtension())
                .fileSize(file.getFileSize())
                .s3Key(file.getS3Key())
                .mimeType(file.getMimeType())
                .access(file.getAccess())
                .visibility(file.getVisibility())
                .videoSource(file.getVideoSource())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
        if(extFile.access.equals(FileAccess.PREMIUM)) extFile.setS3Key(null);
        return extFile;
    }
}
