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
    @NotEmpty(message = "{role_permission.permission_ids.not_empty}")
    List<@NotBlank(message = "{role_permission.permission_id.invalid}") String> permissionIds;
}
