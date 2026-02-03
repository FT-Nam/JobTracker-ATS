package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobSkillResponse {
    String skillId;
    String name;
    String category;
    Boolean isRequired;
    String proficiencyLevel;
}
