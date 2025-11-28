package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewTypeRequest {
    @NotBlank(message = "interview_type.name.not_blank")
    @Size(max = 50, message = "interview_type.name.size")
    String name;

    @NotBlank(message = "interview_type.display_name.not_blank")
    @Size(max = 100, message = "interview_type.display_name.size")
    String displayName;

    @Size(max = 255, message = "interview_type.description.size")
    String description;

    Boolean isActive;
}

