package com.jobtracker.jobtracker_app.dto.requests.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobSkillUpdateRequest {

    Boolean isRequired;

    String proficiencyLevel;
}


