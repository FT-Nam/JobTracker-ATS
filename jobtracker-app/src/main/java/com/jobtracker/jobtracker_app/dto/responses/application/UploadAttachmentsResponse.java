package com.jobtracker.jobtracker_app.dto.responses.application;

import com.jobtracker.jobtracker_app.enums.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadAttachmentsResponse {
    String id;
    String applicationId;
    String fileName;
    AttachmentType attachmentType;
    Long fileSize;
    LocalDateTime uploadedAt;
}
