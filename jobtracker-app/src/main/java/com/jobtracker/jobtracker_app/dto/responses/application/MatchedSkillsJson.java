package com.jobtracker.jobtracker_app.dto.responses.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchedSkillsJson {

    private List<String> matchedRequired;
    private List<String> missingRequired;
    private List<String> matchedOptional;
    private List<String> missingOptional;
}
