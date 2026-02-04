package com.jobtracker.jobtracker_app.dto.requests;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleCreationRequest {
    @NotBlank(message = "Role name must not be blank")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    String description;

    Boolean isActive;
}
