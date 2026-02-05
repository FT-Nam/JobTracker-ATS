package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Resume extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false, length = 255)
    String name;

    @Column(name = "original_filename", nullable = false, length = 255)
    String originalFilename;

    @Column(name = "file_path", nullable = false, length = 500)
    String filePath;

    @Column(name = "file_size", nullable = false)
    Long fileSize;

    @Column(name = "file_type", nullable = false, length = 100)
    String fileType;

    @Column(length = 50)
    String version = "1.0";

    @Column(name = "is_default", nullable = false)
    Boolean isDefault = false;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "JSON")
    String tags;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;

    @Column(name = "uploaded_at")
    LocalDateTime uploadedAt;
}




