package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.PartialAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_user_skill", columnNames = {"user_id", "skill_id"}))
public class UserSkill extends PartialAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    Skill skill;

    @Column(name = "proficiency_level", nullable = false, length = 50)
    String proficiencyLevel;

    @Column(name = "years_of_experience", precision = 3, scale = 1)
    BigDecimal yearsOfExperience;

    @Column(name = "is_verified", nullable = false)
    Boolean isVerified = false;
}




