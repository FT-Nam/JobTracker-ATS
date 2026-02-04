package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionRequest {
    @NotBlank(message = "Permission ID is required")
    String permissionId;
}
