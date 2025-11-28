package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriorityRequest {
    @NotBlank(message = "priority.name.not_blank")
    @Size(max = 50, message = "priority.name.size")
    String name;

    @NotBlank(message = "priority.display_name.not_blank")
    @Size(max = 100, message = "priority.display_name.size")
    String displayName;

    @NotNull(message = "priority.level.not_null")
    @Min(value = 1, message = "priority.level.min")
    @Max(value = 5, message = "priority.level.max")
    Integer level;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "priority.color.invalid")
    String color;

    @Size(max = 255, message = "priority.description.size")
    String description;

    Boolean isActive;
}

