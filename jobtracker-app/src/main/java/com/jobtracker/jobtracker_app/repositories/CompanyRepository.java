package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, String> {
}
