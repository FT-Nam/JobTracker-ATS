package com.jobtracker.jobtracker_app.dto.requests.application;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationCreateRequest {

    @NotBlank(message = "{application.job_id.not_blank}")
    String jobId;

    @NotBlank(message = "{application.candidate_name.not_blank}")
    String candidateName;

    @NotBlank(message = "{application.candidate_email.not_blank}")
    @Email(message = "{application.candidate_email.invalid}")
    String candidateEmail;

    String candidatePhone;

    @NotBlank(message = "{application.status_id.not_blank}")
    String statusId;

    String source;

    @NotNull(message = "{application.applied_date.not_null}")
    LocalDate appliedDate;

    String coverLetter;

    String notes;
}

