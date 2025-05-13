package com.vi5hnu.codesprout.models.folderStructure;

import com.vi5hnu.codesprout.enums.FileExtension;
import com.vi5hnu.codesprout.enums.Visibility;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto implements FSItemDto {
    private String id;
    private String ownerId;
    private String folderId;
    private String name;
    private FileExtension fileExtension;
    private String mimeType;
    private String s3Key;
    private Long fileSize;//in bytes
    private Visibility visibility = Visibility.PRIVATE;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
