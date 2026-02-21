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
public class SkillRequest {
    @NotBlank(message = "{skill.name.not_blank}")
    @Size(max = 100, message = "{skill.name.size}")
    String name;

    @NotBlank(message = "{skill.category.not_blank}")
    @Size(max = 50, message = "{skill.category.size}")
    @Pattern(
            regexp = "PROGRAMMING|FRAMEWORK|DATABASE|TOOL|LANGUAGE|SOFT_SKILL|OTHER",
            message = "{skill.category.pattern}")
    String category;

    String description;

    Boolean isActive;
}




