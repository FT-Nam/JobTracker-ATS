package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.UserSessionRequest;
import com.jobtracker.jobtracker_app.dto.responses.UserSessionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserSessionService {
    UserSessionResponse create(UserSessionRequest request);
    UserSessionResponse getById(String id);
    Page<UserSessionResponse> getAll(Pageable pageable);
    UserSessionResponse update(String id, UserSessionRequest request);
    void delete(String id);
}





