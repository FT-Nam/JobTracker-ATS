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
public class InterviewStatusRequest {
    @NotBlank(message = "interview_status.name.not_blank")
    @Size(max = 50, message = "interview_status.name.size")
    String name;

    @NotBlank(message = "interview_status.display_name.not_blank")
    @Size(max = 100, message = "interview_status.display_name.size")
    String displayName;

    @Size(max = 255, message = "interview_status.description.size")
    String description;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "interview_status.color.invalid")
    String color;

    Boolean isActive;
}

