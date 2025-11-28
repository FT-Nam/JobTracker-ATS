package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExperienceLevelRequest {
    @NotBlank(message = "experience_level.name.not_blank")
    @Size(max = 50, message = "experience_level.name.size")
    String name;

    @NotBlank(message = "experience_level.display_name.not_blank")
    @Size(max = 100, message = "experience_level.display_name.size")
    String displayName;

    @Min(value = 0, message = "experience_level.min_years.min")
    Integer minYears;

    @Min(value = 0, message = "experience_level.max_years.min")
    Integer maxYears;

    @Size(max = 255, message = "experience_level.description.size")
    String description;

    Boolean isActive;

    @AssertTrue(message = "experience_level.max_years.invalid")
    @JsonIgnore
    public boolean isMaxYearsValid() {
        if (minYears == null || maxYears == null) {
            return true;
        }
        return maxYears >= minYears;
    }
}

