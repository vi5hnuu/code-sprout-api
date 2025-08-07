package com.vi5hnu.codesprout.models;

import com.vi5hnu.codesprout.enums.Visibility;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFileFromContentRequest {
    private String folderId;
    @NotNull(message = "content cannot be null")
    private String content;

    @NotNull(message = "file name cannot be null")
    private String fileName;
    private Visibility visibility = Visibility.PRIVATE;
}
