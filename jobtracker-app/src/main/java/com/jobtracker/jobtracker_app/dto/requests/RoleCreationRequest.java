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
    @NotBlank(message = "{role.name.not_blank}")
    @Size(max = 50, message = "{role.name.size}")
    String name;

    @Size(max = 255, message = "{role.description.size}")
    String description;

    Boolean isActive;
}
