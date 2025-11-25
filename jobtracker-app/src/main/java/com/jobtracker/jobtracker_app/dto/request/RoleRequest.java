package com.jobtracker.jobtracker_app.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(max = 50, message = "Tên vai trò không được vượt quá 50 ký tự")
    String name;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    String description;

    @NotEmpty(message = "Vai trò phải có ít nhất 1 quyền")
    List<@NotBlank(message = "ID quyền không hợp lệ") String> permissionIds;

    Boolean isActive;
}
