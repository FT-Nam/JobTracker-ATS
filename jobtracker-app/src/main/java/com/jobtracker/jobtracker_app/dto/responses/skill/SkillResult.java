package com.jobtracker.jobtracker_app.dto.responses.skill;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkillResult {
    List<String> matched;
    List<String> missing;
}
