package com.jobtracker.jobtracker_app.dto.responses.attachment;

import com.jobtracker.jobtracker_app.enums.AttachmentType;

import java.time.LocalDateTime;

public class AttachmentResponse {
    String id;
    String filename;
    AttachmentType attachmentType;
    Long fileSize;
    LocalDateTime uploadedAt;
}
