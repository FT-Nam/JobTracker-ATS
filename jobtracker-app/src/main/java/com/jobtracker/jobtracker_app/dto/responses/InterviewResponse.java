package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewResponse {
    String id;
    String jobId;
    Integer roundNumber;
    String interviewTypeId;
    LocalDateTime scheduledDate;
    LocalDateTime actualDate;
    Integer durationMinutes;
    String interviewerName;
    String interviewerEmail;
    String interviewerPosition;
    String statusId;
    String resultId;
    String feedback;
    String notes;
    String questionsAsked;
    String answersGiven;
    Integer rating;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
    LocalDateTime deletedAt;
}




