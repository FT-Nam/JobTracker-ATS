package com.jobtracker.jobtracker_app.dto.requests.interview;

import com.jobtracker.jobtracker_app.enums.InterviewResult;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewUpdateRequest {
    LocalDateTime actualDate;

    InterviewStatus status;

    InterviewResult result;

    String feedback;

    String notes;

    String questionsAsked;

    String answersGiven;

    @Min(value = 1, message = "{interview.rating.min}")
    @Max(value = 5, message = "{interview.rating.max}")
    Integer rating;
}
