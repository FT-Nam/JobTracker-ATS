package com.jobtracker.jobtracker_app.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Size(max = 100, message = "First name must not exceed 100 characters")
    String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    String lastName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must contain 10 to 15 digits")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    String phone;

    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    String avatarUrl;

    Boolean isActive;

    String roleId;
}
