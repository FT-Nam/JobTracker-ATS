package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, String> {

    @Query("SELECT c FROM Company c WHERE c.deletedAt IS NULL " +
            "AND (:industry IS NULL OR :industry = '' OR c.industry = :industry) " +
            "AND (:search IS NULL OR :search = '' " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR (c.description IS NOT NULL AND LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))))")
    Page<Company> searchCompanies(
            @Param("industry") String industry,
            @Param("search") String search,
            Pageable pageable
    );
}
