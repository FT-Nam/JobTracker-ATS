package com.jobtracker.jobtracker_app.dto.responses;

import com.jobtracker.jobtracker_app.entities.Attachment;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentResponse {
    String id;
    String jobId;
    String userId;
    String filename;
    String originalFilename;
    String filePath;
    Long fileSize;
    String fileType;
    Attachment.AttachmentType attachmentType;
    String description;
    Boolean isPublic;
    LocalDateTime uploadedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
    LocalDateTime deletedAt;
}




