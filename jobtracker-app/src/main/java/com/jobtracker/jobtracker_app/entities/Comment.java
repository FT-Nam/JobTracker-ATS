package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    Application application;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
    String commentText;

    @Column(name = "is_internal", nullable = false)
    Boolean isInternal = true;
}

