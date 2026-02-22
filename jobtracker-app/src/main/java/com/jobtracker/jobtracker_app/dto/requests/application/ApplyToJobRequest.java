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

    @NotBlank(message = "Candidate name is required")
    String candidateName;

    @NotBlank(message = "Candidate email is required")
    @Email(message = "Invalid email format")
    String candidateEmail;

    @NotBlank(message = "Candidate phone is required")
    String candidatePhone;

    String coverLetter;

    @NotNull(message = "Resume file is required")
    MultipartFile resume;
}