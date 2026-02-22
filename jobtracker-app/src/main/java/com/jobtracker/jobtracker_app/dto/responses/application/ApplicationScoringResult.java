package com.jobtracker.jobtracker_app.dto.responses.application;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationScoringResult {
    int matchScore;
    int matchedRequiredCount;
    int totalRequiredCount;
    int matchedOptionalCount;
    int totalOptionalCount;
    List<String> matchedRequiredSkills;
    List<String> missingRequiredSkills;
    List<String> matchedOptionalSkills;
    List<String> missingOptionalSkills;
}
