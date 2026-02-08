package com.jobtracker.jobtracker_app.dto.responses.job;

import com.jobtracker.jobtracker_app.enums.JobStatus;
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
    JobStatus jobStatus;
    LocalDateTime publishedAt;
    LocalDateTime expiresAt;
    LocalDateTime updatedAt;
}
