package com.jobtracker.jobtracker_app.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum SystemVariable {
    // Company & User
    COMPANY_NAME("company_name"),
    USER_EMAIL("user_email"),
    USER_FIRST_NAME("user_first_name"),
    USER_LAST_NAME("user_last_name"),
    USER_NAME("user_name"),

    // Auth links
    INVITE_LINK("invite_link"),
    VERIFICATION_LINK("verification_link"),
    RESET_LINK("reset_link"),

    // Application Workflow
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
