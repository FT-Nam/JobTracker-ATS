package com.jobtracker.jobtracker_app.dto.requests.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateProfileRequest {
    @Size(max = 100, message = "{user.first_name.size}")
    String firstName;

    @Size(max = 100, message = "{user.last_name.size}")
    String lastName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "{user.phone.pattern}")
    @Size(max = 20, message = "{user.phone.size}")
    String phone;
}
