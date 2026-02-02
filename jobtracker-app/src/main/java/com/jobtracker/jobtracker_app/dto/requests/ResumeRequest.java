package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResumeRequest {
    @NotBlank(message = "resume.user_id.not_blank")
    String userId;

    @NotBlank(message = "resume.name.not_blank")
    @Size(max = 255, message = "resume.name.size")
    String name;

    @NotBlank(message = "resume.original_filename.not_blank")
    @Size(max = 255, message = "resume.original_filename.size")
    String originalFilename;

    @NotBlank(message = "resume.file_path.not_blank")
    @Size(max = 500, message = "resume.file_path.size")
    String filePath;

    @NotNull(message = "resume.file_size.not_null")
    Long fileSize;

    @NotBlank(message = "resume.file_type.not_blank")
    @Size(max = 100, message = "resume.file_type.size")
    String fileType;

    @Size(max = 50, message = "resume.version.size")
    String version;

    Boolean isDefault;

    String description;

    String tags;

    Boolean isActive;

    LocalDateTime uploadedAt;
}




