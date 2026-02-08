package com.jobtracker.jobtracker_app.dto.requests.job;

import com.jobtracker.jobtracker_app.enums.JobStatus;
import com.jobtracker.jobtracker_app.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class JobUpdateRequest {
    @NotBlank(message = "job.title.not_blank")
    @Size(max = 255, message = "job.title.size")
    String title;

    @NotBlank(message = "job.position.not_blank")
    @Size(max = 255, message = "job.position.size")
    String position;

    JobType jobType;

    String location;

    BigDecimal salaryMin;

    BigDecimal salaryMax;

    String currency;

    JobStatus jobStatus;

    LocalDate deadlineDate;

    String jobDescription;

    String requirements;

    String benefits;

    String jobUrl;

    Boolean isRemote;
}
