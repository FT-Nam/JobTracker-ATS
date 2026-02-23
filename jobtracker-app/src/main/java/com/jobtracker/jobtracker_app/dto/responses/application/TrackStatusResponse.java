package com.jobtracker.jobtracker_app.dto.responses.application;

import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackStatusResponse {
    String id;
    String jobTitle;
    String candidateName;
    String candidateEmail;
    ApplicationStatusDetail status;
    LocalDate appliedDate;
    LocalDateTime updatedAt;
}
