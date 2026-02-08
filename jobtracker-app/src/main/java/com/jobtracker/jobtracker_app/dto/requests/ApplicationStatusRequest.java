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
    @NotBlank(message = "Application status name must not be blank")
    @Size(max = 50, message = "Application status name must not exceed 50 characters")
    String name;

    @NotBlank(message = "Display name must not be blank")
    @Size(max = 100, message = "Display name must not exceed 100 characters")
    String displayName;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    String description;

    @Size(max = 7, message = "Color must not exceed 7 characters")
    String color;

    Integer sortOrder;

    Boolean isActive;
}

