package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationStatusRequest {
    @NotBlank(message = "{application_status.name.not_blank}")
    @Size(max = 50, message = "{application_status.name.size}")
    String name;

    @NotBlank(message = "{application_status.display_name.not_blank}")
    @Size(max = 100, message = "{application_status.display_name.size}")
    String displayName;

    @Size(max = 255, message = "{application_status.description.size}")
    String description;

    @Size(max = 7, message = "{application_status.color.size}")
    String color;

    Integer sortOrder;

    Boolean isActive;
}

