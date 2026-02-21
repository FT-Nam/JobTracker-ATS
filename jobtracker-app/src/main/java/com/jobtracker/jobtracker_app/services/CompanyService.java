package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.company.CompanyFilterRequest;
import com.jobtracker.jobtracker_app.dto.requests.company.CompanyUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.CompanyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {
    CompanyResponse getById(String id);
    Page<CompanyResponse> getAll(CompanyFilterRequest request, Pageable pageable);
    CompanyResponse update(String id, CompanyUpdateRequest request);
    CompanyResponse setVerified(String id, boolean isVerified);
    void delete(String id);
}
