package com.jobtracker.jobtracker_app.dto.requests;

import com.jobtracker.jobtracker_app.enums.InterviewType;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import com.jobtracker.jobtracker_app.enums.InterviewResult;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewRequest {
    @NotBlank(message = "interview.application_id.not_blank")
    String applicationId;

    @NotBlank(message = "interview.job_id.not_blank")
    String jobId;

    @NotBlank(message = "interview.company_id.not_blank")
    String companyId;

    @NotNull(message = "interview.round_number.not_null")
    @Min(value = 1, message = "interview.round_number.min")
    Integer roundNumber;

    @NotNull(message = "interview.interview_type.not_null")
    InterviewType interviewType;

    @NotNull(message = "interview.scheduled_date.not_null")
    LocalDateTime scheduledDate;

    LocalDateTime actualDate;

    @Min(value = 1, message = "interview.duration_minutes.min")
    Integer durationMinutes;

    @Size(max = 255, message = "interview.interviewer_name.size")
    String interviewerName;

    @Size(max = 255, message = "interview.interviewer_email.size")
    String interviewerEmail;

    @Size(max = 255, message = "interview.interviewer_position.size")
    String interviewerPosition;

    InterviewStatus status;

    InterviewResult result;

    String feedback;

    String notes;

    String questionsAsked;

    String answersGiven;

    @Min(value = 1, message = "interview.rating.min")
    @Max(value = 5, message = "interview.rating.max")
    Integer rating;

    String meetingLink;

    String location;
}




