package com.jobtracker.jobtracker_app.dto.requests.interview;

import com.jobtracker.jobtracker_app.enums.InterviewType;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import com.jobtracker.jobtracker_app.enums.InterviewResult;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewCreationRequest {
    @NotNull(message = "{interview.round_number.not_null}")
    @Min(value = 1, message = "{interview.round_number.min}")
    Integer roundNumber;

    @NotNull(message = "{interview.interview_type.not_null}")
    InterviewType interviewType;

    @NotNull(message = "{interview.scheduled_date.not_null}")
    LocalDateTime scheduledDate;

    @Min(value = 1, message = "{interview.duration_minutes.min}")
    Integer durationMinutes;

    @NotEmpty(message = "{interview.interviewer_ids.not_empty}")
    @Size(min = 1, message = "{interview.interviewer_ids.min_size}")
    Set<String> interviewerIds;

    String primaryInterviewerId;

    InterviewStatus status;

    String notes;

    String meetingLink;

    String location;
}




