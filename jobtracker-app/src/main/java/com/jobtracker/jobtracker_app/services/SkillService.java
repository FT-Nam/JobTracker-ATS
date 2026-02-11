package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.SkillRequest;
import com.jobtracker.jobtracker_app.dto.responses.SkillResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SkillService {
    SkillResponse create(SkillRequest request);
    SkillResponse getById(String id);
    Page<SkillResponse> getAll(Pageable pageable);
    SkillResponse update(String id, SkillRequest request);
    void delete(String id);
}





