package com.jobtracker.jobtracker_app.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionsUpdateResponse {
    String roleId;
    List<String> permissionIds;
    LocalDateTime updatedAt;
}

