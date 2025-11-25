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
    @Size(max = 100, message = "Họ không được vượt quá 100 ký tự")
    String firstName;

    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    String lastName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại phải là số và có từ 10 đến 15 ký tự")
    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    String phone;

    @Size(max = 500, message = "Đường dẫn ảnh đại diện không được vượt quá 500 ký tự")
    String avatarUrl;

    Boolean isActive;

    String roleId;
}
