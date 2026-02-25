package com.jobtracker.jobtracker_app.dto.requests.interview;

import com.jobtracker.jobtracker_app.enums.InterviewResult;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewUpdateRequest {
    LocalDateTime scheduledDate;

    LocalDateTime actualDate;

    InterviewResult result;

    @Min(value = 1, message = "{interview.duration_minutes.min}")
    Integer durationMinutes;

    String feedback;

    String notes;

    String questionsAsked;

    String answersGiven;

    @Min(value = 1, message = "{interview.rating.min}")
    @Max(value = 5, message = "{interview.rating.max}")
    Integer rating;

    Set<String> interviewerIds;

    String primaryInterviewerId;
}
