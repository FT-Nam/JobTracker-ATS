package com.jobtracker.jobtracker_app.entity.base;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartialAuditEntity extends BaseEntity {
    @Column(name = "created_by")
    @CreatedBy
    String createdBy;

    @Column(name = "is_deleted", nullable = false)
    Boolean isDeleted = false;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }
}
