package com.jobtracker.jobtracker_app.dto.requests.application;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplyToJobRequest {

    @NotBlank(message = "{application.candidate_name.not_blank}")
    String candidateName;

    @NotBlank(message = "{application.candidate_email.not_blank}")
    @Email(message = "{application.candidate_email.invalid}")
    String candidateEmail;

    @NotBlank(message = "{application.candidate_phone.not_blank}")
    String candidatePhone;

    String coverLetter;

    @NotNull(message = "{application.resume_file.not_null}")
    MultipartFile resume;
}