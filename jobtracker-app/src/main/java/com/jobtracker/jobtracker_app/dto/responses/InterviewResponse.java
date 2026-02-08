package com.jobtracker.jobtracker_app.dto.responses;

import com.jobtracker.jobtracker_app.enums.InterviewType;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import com.jobtracker.jobtracker_app.enums.InterviewResult;
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
    String applicationId;
    String jobId;
    String companyId;
    Integer roundNumber;
    InterviewType interviewType;
    LocalDateTime scheduledDate;
    LocalDateTime actualDate;
    Integer durationMinutes;
    String interviewerName;
    String interviewerEmail;
    String interviewerPosition;
    InterviewStatus status;
    InterviewResult result;
    String feedback;
    String notes;
    String questionsAsked;
    String answersGiven;
    Integer rating;
    String meetingLink;
    String location;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
    LocalDateTime deletedAt;
}




