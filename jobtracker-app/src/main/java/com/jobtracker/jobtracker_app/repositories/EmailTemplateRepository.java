package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, String> {

    Optional<EmailTemplate> findByCodeAndCompany_IdAndIsActiveTrueAndDeletedAtIsNull(String code, String companyId);

    Optional<EmailTemplate> findByCodeAndCompanyIsNullAndIsActiveTrueAndDeletedAtIsNull(String code);

    List<EmailTemplate> findByCompany_IdAndIsActiveTrueAndDeletedAtIsNull(String companyId);

    List<EmailTemplate> findByCompanyIsNullAndIsActiveTrueAndDeletedAtIsNull();

    Optional<EmailTemplate> findByCodeAndCompany_IdAndDeletedAtIsNull(String code, String companyId);

    Optional<EmailTemplate> findByCodeAndCompanyIsNullAndDeletedAtIsNull(String code);

    boolean existsByCodeAndCompany_IdAndDeletedAtIsNull(String code, String companyId);

    boolean existsByCodeAndCompanyIsNullAndDeletedAtIsNull(String code);
}
