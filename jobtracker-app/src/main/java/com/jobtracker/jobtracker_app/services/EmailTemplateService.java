package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.email.*;
import com.jobtracker.jobtracker_app.dto.responses.email.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailTemplateService {

    EmailTemplateResponse create(EmailTemplateCreationRequest request);

    EmailTemplateDetailResponse getById(String id);

    Page<EmailTemplateResponse> getAll(String code, String name, Boolean isActive, Pageable pageable);

    EmailTemplateResponse update(String id, EmailTemplateUpdateRequest request);

    void delete(String id);

    EmailTemplatePreviewResponse preview(String id, EmailTemplatePreviewRequest request);

    void sendTest(String id, EmailTemplateSendTestRequest request);
}
