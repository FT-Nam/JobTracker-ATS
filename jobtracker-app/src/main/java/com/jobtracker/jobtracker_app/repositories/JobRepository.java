package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.enums.JobStatus;
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
    @Query("SELECT j FROM Job j " +
            "WHERE j.company.id = :companyId " +
            "AND j.deletedAt IS NULL " +
            "AND (:jobStatus IS NULL OR j.jobStatus = :jobStatus)" +
            "AND (:isRemote IS NULL OR j.isRemote = :isRemote)" +
            "AND (:search IS NULL" +
            "OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))" +
            "OR LOWER(j.position) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> searchJobs(
            @Param("companyId") String companyId,
            @Param("jobStatus") JobStatus jobStatus,
            @Param("isRemote") Boolean isRemote,
            @Param("search") String search,
            Pageable pageable
    );

//    @Query("SELECT j FROM Job j WHERE j.id = :id AND j.deletedAt IS NULL")
    Optional<Job> findByIdAndDeletedAtIsNull(String id);

}




