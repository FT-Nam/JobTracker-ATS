package com.jobtracker.jobtracker_app.dto.requests.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobTypeRequest {
    @NotBlank(message = "job_type.name.not_blank")
    @Size(max = 50, message = "job_type.name.size")
    String name;

    @NotBlank(message = "job_type.display_name.not_blank")
    @Size(max = 100, message = "job_type.display_name.size")
    String displayName;

    @Size(max = 255, message = "job_type.description.size")
    String description;

    Boolean isActive;
}

