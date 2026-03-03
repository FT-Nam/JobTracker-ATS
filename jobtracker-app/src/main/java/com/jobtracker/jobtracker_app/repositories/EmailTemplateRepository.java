package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Optional<EmailTemplate> findByIdAndCompany_IdAndDeletedAtIsNull(String id, String companyId);

    Optional<EmailTemplate> findByIdAndCompanyIsNullAndDeletedAtIsNull(String id);

    // Sort theo company trước, nếu = nhau thì sort theo code
    @Query("""
            SELECT e FROM EmailTemplate e
            WHERE e.deletedAt IS NULL
            AND (e.company.id = :companyId OR e.company IS NULL)
            AND (:code IS NULL OR :code = '' OR e.code = :code)
            AND (:name IS NULL OR :name = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:isActive IS NULL OR e.isActive = :isActive)
            ORDER BY e.company.id ASC NULLS FIRST, e.code ASC
            """)
    Page<EmailTemplate> searchTemplates(
            @Param("companyId") String companyId,
            @Param("code") String code,
            @Param("name") String name,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}
