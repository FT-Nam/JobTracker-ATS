package com.jobtracker.jobtracker_app.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.JobStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.JobStatusResponse;
import com.jobtracker.jobtracker_app.entities.JobStatus;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.JobStatusMapper;
import com.jobtracker.jobtracker_app.repositories.JobStatusRepository;
import com.jobtracker.jobtracker_app.services.JobStatusService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class JobStatusServiceImpl implements JobStatusService {
    JobStatusRepository jobStatusRepository;
    JobStatusMapper jobStatusMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_STATUS_CREATE')")
    public JobStatusResponse create(JobStatusRequest request) {
        validateNameUnique(request.getName(), null);
        JobStatus jobStatus = jobStatusMapper.toJobStatus(request);
        return jobStatusMapper.toJobStatusResponse(jobStatusRepository.save(jobStatus));
    }

    @Override
    public JobStatusResponse getById(String id) {
        return jobStatusMapper.toJobStatusResponse(findById(id));
    }

    @Override
    public Page<JobStatusResponse> getAll(Pageable pageable) {
        return jobStatusRepository.findAll(pageable).map(jobStatusMapper::toJobStatusResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_STATUS_UPDATE')")
    public JobStatusResponse update(String id, JobStatusRequest request) {
        JobStatus jobStatus = findById(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(jobStatus.getName())) {
            validateNameUnique(request.getName(), id);
        }
        jobStatusMapper.updateJobStatus(jobStatus, request);
        return jobStatusMapper.toJobStatusResponse(jobStatusRepository.save(jobStatus));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_STATUS_DELETE')")
    public void delete(String id) {
        JobStatus jobStatus = findById(id);
        jobStatus.softDelete();
        jobStatusRepository.save(jobStatus);
    }

    private JobStatus findById(String id) {
        return jobStatusRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_STATUS_NOT_EXISTED));
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? jobStatusRepository.existsByNameIgnoreCase(name)
                : jobStatusRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}

