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
public class JobSkillCreationRequest {

    @NotBlank(message = "userSkill.skillId.not_blank")
    String skillId;

    @NotNull(message = "userSkill.isRequired.not_null")
    Boolean isRequired;

    @NotBlank(message = "userSkill.proficiencyLevel.not_blank")
    String proficiencyLevel;
}


