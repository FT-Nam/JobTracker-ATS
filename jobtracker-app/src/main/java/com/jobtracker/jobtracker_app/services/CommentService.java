package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.CommentCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.CommentUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Page<CommentResponse> getComments(String applicationId, Boolean isInternal, Pageable pageable);

    CommentResponse create(String applicationId, CommentCreationRequest request);

    CommentResponse update(String applicationId, String commentId, CommentUpdateRequest request);

    void delete(String applicationId, String commentId);
}
