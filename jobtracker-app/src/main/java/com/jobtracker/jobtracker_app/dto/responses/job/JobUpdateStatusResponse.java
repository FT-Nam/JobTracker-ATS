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
public class JobUpdateStatusResponse {
    String id;
    String status;
    LocalDate offerDate;
    String notes;
    LocalDateTime updatedAt;
}
