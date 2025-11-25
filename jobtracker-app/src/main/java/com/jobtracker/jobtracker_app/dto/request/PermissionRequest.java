package com.jobtracker.jobtracker_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {

    @NotBlank(message = "Tên quyền không được để trống")
    @Size(max = 50, message = "Tên quyền không được vượt quá 50 ký tự")
    String name;

    @NotBlank(message = "Resource không được để trống")
    @Size(max = 100, message = "Resource không được vượt quá 100 ký tự")
    String resource;

    @NotBlank(message = "Action không được để trống")
    @Size(max = 50, message = "Action không được vượt quá 50 ký tự")
    String action;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    String description;

    Boolean isActive;
}
