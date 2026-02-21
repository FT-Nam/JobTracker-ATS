package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Size(max = 100, message = "{user.first_name.size}")
    String firstName;

    @Size(max = 100, message = "{user.last_name.size}")
    String lastName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "{user.phone.pattern}")
    @Size(max = 20, message = "{user.phone.size}")
    String phone;

    @Size(max = 500, message = "{user.avatar_url.size}")
    String avatarUrl;

    Boolean isActive;

    String roleId;
}
