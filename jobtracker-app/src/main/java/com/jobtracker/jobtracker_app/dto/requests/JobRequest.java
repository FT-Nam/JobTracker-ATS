package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobRequest {
    @NotBlank(message = "job.user_id.not_blank")
    String userId;

    @NotBlank(message = "job.company_id.not_blank")
    String companyId;

    @NotBlank(message = "job.title.not_blank")
    @Size(max = 255, message = "job.title.size")
    String title;

    @NotBlank(message = "job.position.not_blank")
    @Size(max = 255, message = "job.position.size")
    String position;

    @NotBlank(message = "job.job_type_id.not_blank")
    String jobTypeId;

    @Size(max = 255, message = "job.location.size")
    String location;

    @DecimalMin(value = "0.0", message = "job.salary_min.min")
    BigDecimal salaryMin;

    @DecimalMin(value = "0.0", message = "job.salary_max.min")
    BigDecimal salaryMax;

    @Pattern(regexp = "USD|VND|EUR|GBP|JPY", message = "job.currency.pattern")
    String currency;

    @NotBlank(message = "job.status_id.not_blank")
    String statusId;

    LocalDate applicationDate;

    LocalDate deadlineDate;

    LocalDate interviewDate;

    LocalDate offerDate;

    String jobDescription;

    String requirements;

    String benefits;

    @Size(max = 500, message = "job.job_url.size")
    @Pattern(regexp = "^(https?://).*$", message = "job.job_url.pattern")
    String jobUrl;

    String notes;

    @NotBlank(message = "job.priority_id.not_blank")
    String priorityId;

    Boolean isRemote;

    String experienceLevelId;
}




