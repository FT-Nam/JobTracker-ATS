package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "email_templates",
        uniqueConstraints = @UniqueConstraint(name = "uniq_company_code", columnNames = {"company_id", "code"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailTemplate extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    Company company;

    @Column(nullable = false, length = 100)
    String code;

    @Column(nullable = false, length = 255)
    String name;

    @Column(nullable = false, length = 500)
    String subject;

    @Column(name = "html_content", nullable = false, columnDefinition = "MEDIUMTEXT")
    String htmlContent;

    @Column(columnDefinition = "JSON")
    String variables;

    @Column(name = "from_name", length = 255)
    String fromName;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;

    public boolean isGlobal() {
        return company == null;
    }
}
