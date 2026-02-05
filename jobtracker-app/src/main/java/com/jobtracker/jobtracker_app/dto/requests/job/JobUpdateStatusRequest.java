package com.jobtracker.jobtracker_app.dto.requests.job;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobUpdateStatusRequest {
    @NotBlank(message = "job_status.name.not_blank")
    String status;

    LocalDate offerDate;

    String notes;
}
