package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionsResponse {
    String permissionId;
    String name;
    String resource;
    String action;
}
