package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.JobRequest;
import com.jobtracker.jobtracker_app.dto.responses.JobResponse;
import com.jobtracker.jobtracker_app.dto.responses.JobSkillResponse;
import com.jobtracker.jobtracker_app.dto.responses.SkillResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    JobResponse create(JobRequest request);
    JobResponse getById(String id);
    Page<JobResponse> getAll(Pageable pageable);
    JobResponse update(String id, JobRequest request);
    JobResponse updateStatus(String id, String statusId);
    List<JobSkillResponse> getJobSkills(String jobId);
    void delete(String id);
}




