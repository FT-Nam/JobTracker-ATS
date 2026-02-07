package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, String> {
    @EntityGraph(attributePaths = {
            "company",
            "status",
            "jobType",
            "priority",
            "experienceLevel"
    })
    @Query("SELECT j FROM Job j WHERE j.user.id = :userId AND j.deletedAt IS NULL")
    Page<Job> findAllByUserIdAndNotDeleted(@Param("userId") String userId, Pageable pageable);

//    @Query("SELECT j FROM Job j WHERE j.id = :id AND j.deletedAt IS NULL")
    Optional<Job> findByIdAndDeletedAtIsNull(@Param("id") String id);

}




