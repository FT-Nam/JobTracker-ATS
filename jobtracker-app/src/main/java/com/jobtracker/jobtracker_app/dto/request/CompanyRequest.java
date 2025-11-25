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
    @NotBlank(message = "Company name must not be blank")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    String name;

    @Size(max = 500, message = "Website URL must not exceed 500 characters")
    @Pattern(regexp = "^(https?://).*$", message = "Website must start with http:// or https://")
    String website;

    @Size(max = 100, message = "Industry must not exceed 100 characters")
    String industry;

    @Size(max = 50, message = "Company size must not exceed 50 characters")
    @Pattern(
            regexp = "STARTUP|SMALL|MEDIUM|LARGE|ENTERPRISE",
            message = "Size must be one of: STARTUP, SMALL, MEDIUM, LARGE, ENTERPRISE")
    String size;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    String location;

    String description;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    @Pattern(regexp = "^(https?://).*$", message = "Logo URL must start with http:// or https://")
    String logoUrl;

    Boolean isVerified;
}
