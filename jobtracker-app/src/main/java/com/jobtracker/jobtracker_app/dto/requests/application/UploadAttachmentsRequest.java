package com.jobtracker.jobtracker_app.dto.requests.application;

import com.jobtracker.jobtracker_app.enums.AttachmentType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadAttachmentsRequest {
    @NotNull(message = "{attachment.file.not_null}")
    MultipartFile file;

    @NotNull(message = "{attachment.attachment_type.not_null}")
    AttachmentType attachmentType;

    String description;
}
