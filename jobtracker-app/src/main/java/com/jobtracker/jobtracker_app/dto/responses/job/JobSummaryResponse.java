package com.jobtracker.jobtracker_app.dto.responses.job;

import com.jobtracker.jobtracker_app.enums.JobStatus;
import com.jobtracker.jobtracker_app.enums.JobType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobSummaryResponse {
    String id;
    String title;
    String position;
    JobType jobType;
    String location;
    BigDecimal salaryMin;
    BigDecimal salaryMax;
    String currency;
    JobStatus jobStatus;
    LocalDate deadlineDate;
    LocalDateTime publishedAt;
    LocalDateTime expiresAt;
    Integer viewsCount;
    Integer applicationsCount;
    String jobUrl;
    Boolean isRemote;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
