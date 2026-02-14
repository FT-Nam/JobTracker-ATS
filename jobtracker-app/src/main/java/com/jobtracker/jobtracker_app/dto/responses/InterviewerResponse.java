package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewerResponse {
    String id;
    String name;
    String email;
    Boolean isPrimary;
}

