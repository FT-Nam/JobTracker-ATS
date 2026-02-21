package com.jobtracker.jobtracker_app.dto.requests;

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

    @NotBlank(message = "{permission.name.not_blank}")
    @Size(max = 50, message = "{permission.name.size}")
    String name;

    @NotBlank(message = "{permission.resource.not_blank}")
    @Size(max = 100, message = "{permission.resource.size}")
    String resource;

    @NotBlank(message = "{permission.action.not_blank}")
    @Size(max = 50, message = "{permission.action.size}")
    String action;

    @Size(max = 255, message = "{permission.description.size}")
    String description;

    Boolean isActive;
}
