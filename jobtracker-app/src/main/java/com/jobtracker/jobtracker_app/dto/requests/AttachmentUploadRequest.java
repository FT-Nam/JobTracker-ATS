package com.jobtracker.jobtracker_app.dto.requests;

import com.jobtracker.jobtracker_app.enums.AttachmentType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentUploadRequest {
    MultipartFile file;

    @NotNull(message = "attachment.attachment_type.not_null")
    AttachmentType attachmentType;

    String description;
}
