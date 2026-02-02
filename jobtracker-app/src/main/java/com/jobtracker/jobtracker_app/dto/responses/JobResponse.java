package com.jobtracker.jobtracker_app.dto.responses;

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
public class JobResponse {
    String id;
    String userId;
    String companyId;
    String title;
    String position;
    String jobTypeId;
    String location;
    BigDecimal salaryMin;
    BigDecimal salaryMax;
    String currency;
    String statusId;
    LocalDate applicationDate;
    LocalDate deadlineDate;
    LocalDate interviewDate;
    LocalDate offerDate;
    String jobDescription;
    String requirements;
    String benefits;
    String jobUrl;
    String notes;
    String priorityId;
    Boolean isRemote;
    String experienceLevelId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
    LocalDateTime deletedAt;
}




