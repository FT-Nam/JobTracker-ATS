package com.jobtracker.jobtracker_app.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum ManualVariable {
    OFFER_SALARY("offer_salary"),
    OFFER_START_DATE("offer_start_date"),
    OFFER_EXPIRE_DATE("offer_expire_date"),
    CUSTOM_MESSAGE("custom_message");

    private final String key;

    ManualVariable(String key) {
        this.key = key;
    }

    public static boolean isManual(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }
        for (ManualVariable v : values()) {
            if (v.key.equals(key.trim())) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> allKeys() {
        return Arrays.stream(values())
                .map(ManualVariable::getKey)
                .collect(Collectors.toSet());
    }
}
