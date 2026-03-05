package com.jobtracker.jobtracker_app.dto.requests.role;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleUpdateRequest {
    @Size(max = 255, message = "{role.description.size}")
    String description;

    Boolean isActive;
}
