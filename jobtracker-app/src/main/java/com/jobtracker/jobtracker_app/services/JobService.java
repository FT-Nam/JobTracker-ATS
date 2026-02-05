package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.job.JobCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.job.JobResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobSkillResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobUpdateResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobUpdateStatusResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    JobResponse create(JobCreationRequest request);
    JobResponse getById(String id);
    Page<JobResponse> getAll(Pageable pageable);
    JobUpdateResponse update(String id, JobUpdateRequest request);
    JobUpdateStatusResponse updateStatus(String id, JobUpdateStatusRequest request);
    List<JobSkillResponse> getJobSkills(String jobId);
    void delete(String id);
}




