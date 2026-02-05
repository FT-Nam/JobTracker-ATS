package com.jobtracker.jobtracker_app.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.job.JobTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.job.JobTypeResponse;
import com.jobtracker.jobtracker_app.entities.JobType;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.JobTypeMapper;
import com.jobtracker.jobtracker_app.repositories.JobTypeRepository;
import com.jobtracker.jobtracker_app.services.JobTypeService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class JobTypeServiceImpl implements JobTypeService {
    JobTypeRepository jobTypeRepository;
    JobTypeMapper jobTypeMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_TYPE_CREATE')")
    public JobTypeResponse create(JobTypeRequest request) {
        validateNameUnique(request.getName(), null);
        JobType jobType = jobTypeMapper.toJobType(request);
        return jobTypeMapper.toJobTypeResponse(jobTypeRepository.save(jobType));
    }

    @Override
    public JobTypeResponse getById(String id) {
        return jobTypeMapper.toJobTypeResponse(findById(id));
    }

    @Override
    public Page<JobTypeResponse> getAll(Pageable pageable) {
        return jobTypeRepository.findAll(pageable).map(jobTypeMapper::toJobTypeResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_TYPE_UPDATE')")
    public JobTypeResponse update(String id, JobTypeRequest request) {
        JobType jobType = findById(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(jobType.getName())) {
            validateNameUnique(request.getName(), id);
        }
        jobTypeMapper.updateJobType(jobType, request);
        return jobTypeMapper.toJobTypeResponse(jobTypeRepository.save(jobType));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('JOB_TYPE_DELETE')")
    public void delete(String id) {
        JobType jobType = findById(id);
        jobType.softDelete();
        jobTypeRepository.save(jobType);
    }

    private JobType findById(String id) {
        return jobTypeRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_TYPE_NOT_EXISTED));
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? jobTypeRepository.existsByNameIgnoreCase(name)
                : jobTypeRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}

