package com.jobtracker.jobtracker_app.dto.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobSkillWithName {
    String skillId;
    String skillName;
    Boolean isRequired;
    String proficiencyLevel;
}
