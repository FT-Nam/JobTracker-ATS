package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

    Optional<Application> findByIdAndDeletedAtIsNull(String id);

    Optional<Application> findByIdAndCompanyIdAndDeletedAtIsNull(String id, String companyId);

    boolean existsByIdAndCompanyId(String id, String companyId);

    Optional<Application> findByApplicationTokenAndDeletedAtIsNull(String applicationToken);

    @Query("SELECT a FROM Application a WHERE a.company.id = :companyId AND a.deletedAt IS NULL " +
            "AND (:status IS NULL OR :status = '' OR a.status.name = :status) " +
            "AND (:jobId IS NULL OR :jobId = '' OR a.job.id = :jobId) " +
            "AND (:assignedTo IS NULL OR :assignedTo = '' OR a.assignedTo.id = :assignedTo) " +
            "AND (:search IS NULL OR :search = '' OR LOWER(a.candidateName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.candidateEmail) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:minMatchScore IS NULL OR a.matchScore >= :minMatchScore) " +
            "AND (:maxMatchScore IS NULL OR a.matchScore <= :maxMatchScore)")
    Page<Application> searchApplications(
            @Param("companyId") String companyId,
            @Param("status") String status,
            @Param("jobId") String jobId,
            @Param("assignedTo") String assignedTo,
            @Param("search") String search,
            @Param("minMatchScore") Integer minMatchScore,
            @Param("maxMatchScore") Integer maxMatchScore,
            Pageable pageable);
}

