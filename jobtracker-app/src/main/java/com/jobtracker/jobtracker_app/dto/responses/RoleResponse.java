package com.jobtracker.jobtracker_app.dto.responses;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String id;
    String name;
    String description;
    Set<String> permissionIds;
    List<RolePermissionsResponse> permissions;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
    LocalDateTime deletedAt;
}
