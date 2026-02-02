package com.jobtracker.jobtracker_app.dto.requests;

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
    @NotBlank(message = "company.name.not_blank")
    @Size(max = 255, message = "company.name.size")
    String name;

    @Size(max = 500, message = "company.website.size")
    @Pattern(regexp = "^(https?://).*$", message = "company.website.pattern")
    String website;

    @Size(max = 100, message = "company.industry.size")
    String industry;

    @Size(max = 50, message = "company.size.size")
    @Pattern(
            regexp = "STARTUP|SMALL|MEDIUM|LARGE|ENTERPRISE",
            message = "company.size.pattern")
    String size;

    @Size(max = 255, message = "company.location.size")
    String location;

    String description;

    @Size(max = 500, message = "company.logoUrl.size")
    @Pattern(regexp = "^(https?://).*$", message = "company.logoUrl.pattern")
    String logoUrl;

    Boolean isVerified;
}
