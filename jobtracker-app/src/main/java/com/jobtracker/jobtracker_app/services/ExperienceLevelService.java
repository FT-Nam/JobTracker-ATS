package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.ExperienceLevelRequest;
import com.jobtracker.jobtracker_app.dto.responses.ExperienceLevelResponse;

public interface ExperienceLevelService {
    ExperienceLevelResponse create(ExperienceLevelRequest request);

    ExperienceLevelResponse getById(String id);

    Page<ExperienceLevelResponse> getAll(Pageable pageable);

    ExperienceLevelResponse update(String id, ExperienceLevelRequest request);

    void delete(String id);
}

