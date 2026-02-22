package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.JobSkillWithName;
import com.jobtracker.jobtracker_app.dto.responses.application.ApplicationScoringResult;

import java.util.List;

public interface CVScoringService {
    ApplicationScoringResult score(String cvText, List<JobSkillWithName> jobSkills);

}
