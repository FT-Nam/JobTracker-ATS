package com.jobtracker.jobtracker_app.dto.responses.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatusDetail {
    String name;
    String displayName;
    String color;
}
