package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {
    Optional<Application> findByIdAndDeletedAtIsNull(String id);

    // Multi-tenant: User Company A không xem được cv của User Company B
    // - xyz-999 thuộc company-B → không tìm thấy → throw 404
    // - xyz-999 thuộc company-A → tìm thấy → OK
    Optional<Application> findByIdAndCompanyId(String id, String companyId);

    boolean existsByIdAndCompanyId(String id, String companyId);

    Optional<Application> findByApplicationToken(String applicationToken);
}

