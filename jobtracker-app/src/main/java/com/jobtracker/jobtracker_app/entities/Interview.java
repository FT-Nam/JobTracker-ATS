package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Interview extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    Job job;

    @Column(name = "round_number", nullable = false)
    Integer roundNumber;

    @ManyToOne
    @JoinColumn(name = "interview_type_id", nullable = false)
    InterviewType interviewType;

    @Column(name = "scheduled_date", nullable = false)
    LocalDateTime scheduledDate;

    @Column(name = "actual_date")
    LocalDateTime actualDate;

    @Column(name = "duration_minutes")
    Integer durationMinutes;

    @Column(name = "interviewer_name", length = 255)
    String interviewerName;

    @Column(name = "interviewer_email", length = 255)
    String interviewerEmail;

    @Column(name = "interviewer_position", length = 255)
    String interviewerPosition;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    InterviewStatus status;

    @ManyToOne
    @JoinColumn(name = "result_id")
    InterviewResult result;

    @Column(columnDefinition = "TEXT")
    String feedback;

    @Column(columnDefinition = "TEXT")
    String notes;

    @Column(name = "questions_asked", columnDefinition = "TEXT")
    String questionsAsked;

    @Column(name = "answers_given", columnDefinition = "TEXT")
    String answersGiven;

    @Column
    Integer rating;
}




