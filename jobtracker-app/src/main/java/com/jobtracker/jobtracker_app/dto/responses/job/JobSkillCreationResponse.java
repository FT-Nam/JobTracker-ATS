package com.jobtracker.jobtracker_app.dto.responses.job;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobSkillCreationResponse {
    String id;
    String jobId;
    String skillId;
    String name;
    String category;
    Boolean isRequired;
    String proficiencyLevel;
    LocalDateTime createdAt;
}
