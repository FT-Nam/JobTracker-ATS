package com.jobtracker.jobtracker_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {

    @NotBlank(message = "Permission name must not be blank")
    @Size(max = 50, message = "Permission name must not exceed 50 characters")
    String name;

    @NotBlank(message = "Resource must not be blank")
    @Size(max = 100, message = "Resource must not exceed 100 characters")
    String resource;

    @NotBlank(message = "Action must not be blank")
    @Size(max = 50, message = "Action must not exceed 50 characters")
    String action;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    String description;

    Boolean isActive;
}
