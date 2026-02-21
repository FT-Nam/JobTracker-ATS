package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    @Query("SELECT c FROM Comment c WHERE c.application.id = :applicationId AND c.deletedAt IS NULL " +
            "AND (:isInternal IS NULL OR c.isInternal = :isInternal)")
    Page<Comment> findByApplicationIdAndDeletedAtIsNull(
            @Param("applicationId") String applicationId,
            @Param("isInternal") Boolean isInternal,
            Pageable pageable);

    boolean existsByIdAndDeletedAtIsNull(String id);
}
