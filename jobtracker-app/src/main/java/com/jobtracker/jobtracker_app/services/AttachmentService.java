package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.AttachmentRequest;
import com.jobtracker.jobtracker_app.dto.responses.AttachmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttachmentService {
    AttachmentResponse create(AttachmentRequest request);
    AttachmentResponse getById(String id);
    Page<AttachmentResponse> getAll(Pageable pageable);
    AttachmentResponse update(String id, AttachmentRequest request);
    void delete(String id);
}




