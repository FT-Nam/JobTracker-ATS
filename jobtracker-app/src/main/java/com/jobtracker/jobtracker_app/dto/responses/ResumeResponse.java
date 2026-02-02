package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResumeResponse {
    String id;
    String userId;
    String name;
    String originalFilename;
    String filePath;
    Long fileSize;
    String fileType;
    String version;
    Boolean isDefault;
    String description;
    String tags;
    Boolean isActive;
    LocalDateTime uploadedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
    LocalDateTime deletedAt;
}




