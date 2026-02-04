package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionsRequest {
    @NotEmpty(message = "Role must include at least one permission")
    List<@NotBlank(message = "Permission ID is invalid") String> permissionIds;
}
