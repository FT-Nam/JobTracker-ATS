package com.jobtracker.jobtracker_app.dto.requests.job;

import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobStatusRequest {
    @NotBlank(message = "job_status.name.not_blank")
    @Size(max = 50, message = "job_status.name.size")
    String name;

    @NotBlank(message = "job_status.display_name.not_blank")
    @Size(max = 100, message = "job_status.display_name.size")
    String displayName;

    @Size(max = 255, message = "job_status.description.size")
    String description;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "job_status.color.invalid")
    String color;

    @Min(value = 0, message = "job_status.sort_order.min")
    Integer sortOrder;

    Boolean isActive;
}

