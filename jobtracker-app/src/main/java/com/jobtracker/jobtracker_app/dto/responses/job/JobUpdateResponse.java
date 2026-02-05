package com.jobtracker.jobtracker_app.dto.responses.job;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobUpdateResponse {
    String id;
    String title;
    String position;
    String status;
    LocalDate interviewDate;
    String notes;
    LocalDateTime updatedAt;
}
