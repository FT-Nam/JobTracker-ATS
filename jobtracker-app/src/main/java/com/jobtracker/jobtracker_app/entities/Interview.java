package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import com.jobtracker.jobtracker_app.enums.InterviewType;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import com.jobtracker.jobtracker_app.enums.InterviewResult;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "interviews")
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
    @JoinColumn(name = "application_id", nullable = false)
    Application application;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    Job job;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @Column(name = "round_number", nullable = false)
    Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", nullable = false, length = 50)
    InterviewType interviewType;

    @Column(name = "scheduled_date", nullable = false)
    LocalDateTime scheduledDate;

    @Column(name = "actual_date")
    LocalDateTime actualDate;

    @Column(name = "duration_minutes")
    Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    InterviewStatus status = InterviewStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", length = 50)
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

    @Column(length = 500)
    String meetingLink;

    @Column(length = 255)
    String location;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<InterviewInterviewer> interviewers;
}




