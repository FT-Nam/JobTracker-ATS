package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.audit.AuditLogRequest;
import com.jobtracker.jobtracker_app.dto.responses.audit.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    AuditLogResponse create(AuditLogRequest request);
    AuditLogResponse getById(String id);
    Page<AuditLogResponse> getAll(Pageable pageable);
    AuditLogResponse update(String id, AuditLogRequest request);
    void delete(String id);
}




