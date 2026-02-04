package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleUpdateRequest {
    @Size(max = 255, message = "Description must not exceed 255 characters")
    String description;

    Boolean isActive;
}
