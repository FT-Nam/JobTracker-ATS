package com.jobtracker.jobtracker_app.dto.requests.job;

import com.jobtracker.jobtracker_app.enums.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobUpdateStatusRequest {
    @NotNull(message = "job.job_status.not_null")
    JobStatus jobStatus;
}
