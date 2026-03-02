package com.jobtracker.jobtracker_app.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum SystemVariable {
    COMPANY_NAME("company_name"),
    HR_NAME("hr_name"),
    CANDIDATE_NAME("candidate_name"),
    JOB_TITLE("job_title"),
    APPLICATION_STATUS("application_status"),
    APPLICATION_LINK("application_link"),
    INTERVIEW_TIME("interview_time"),
    INTERVIEW_LOCATION("interview_location"),
    MEETING_LINK("meeting_link");

    private final String key;

    SystemVariable(String key) {
        this.key = key;
    }

    public static boolean isSystem(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }
        for (SystemVariable v : values()) {
            if (v.key.equals(key.trim())) {
                return true;
            }
        }
        return false;
    }

    public static java.util.Set<String> allKeys() {
        return Arrays.stream(values())
                .map(SystemVariable::getKey)
                .collect(Collectors.toSet());
    }
}
