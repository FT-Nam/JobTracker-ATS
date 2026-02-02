package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.CompanyRequest;
import com.jobtracker.jobtracker_app.dto.responses.CompanyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {
    CompanyResponse create(CompanyRequest request);
    CompanyResponse getById(String id);
    Page<CompanyResponse> getAll(Pageable pageable);
    CompanyResponse update(String id, CompanyRequest request);
    void delete(String id);
}
