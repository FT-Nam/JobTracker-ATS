package com.jobtracker.jobtracker_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyRequest {
    @NotBlank(message = "Tên công ty không được để trống")
    @Size(max = 255, message = "Tên công ty tối đa 255 ký tự")
    String name;

    @Size(max = 500, message = "Website tối đa 500 ký tự")
    @Pattern(regexp = "^(https?://).*$", message = "Website phải bắt đầu bằng http:// hoặc https://")
    String website;

    @Size(max = 100, message = "Lĩnh vực hoạt động tối đa 100 ký tự")
    String industry;

    @Size(max = 50, message = "Quy mô công ty tối đa 50 ký tự")
    @Pattern(
            regexp = "STARTUP|SMALL|MEDIUM|LARGE|ENTERPRISE",
            message = "Quy mô phải là một trong: STARTUP, SMALL, MEDIUM, LARGE, ENTERPRISE")
    String size;

    @Size(max = 255, message = "Địa chỉ công ty tối đa 255 ký tự")
    String location;

    String description;

    @Size(max = 500, message = "URL logo tối đa 500 ký tự")
    @Pattern(regexp = "^(https?://).*$", message = "URL logo phải bắt đầu bằng http:// hoặc https://")
    String logoUrl;

    Boolean isVerified;
}
