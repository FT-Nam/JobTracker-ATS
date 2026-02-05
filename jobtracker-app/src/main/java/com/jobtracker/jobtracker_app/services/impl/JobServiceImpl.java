package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.job.JobCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.job.JobResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobSkillResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobUpdateResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobUpdateStatusResponse;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.JobMapper;
import com.jobtracker.jobtracker_app.mappers.JobSkillMapper;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobServiceImpl implements JobService {
    JobRepository jobRepository;
    JobMapper jobMapper;
    UserRepository userRepository;
    CompanyRepository companyRepository;
    JobTypeRepository jobTypeRepository;
    JobStatusRepository jobStatusRepository;
    PriorityRepository priorityRepository;
    ExperienceLevelRepository experienceLevelRepository;
    JobSkillRepository jobSkillRepository;
    JobSkillMapper jobSkillMapper;

    @Override
    @Transactional
    public JobResponse create(JobCreationRequest request) {
        Job job = jobMapper.toJob(request);
        
        job.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        job.setCompany(companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED)));
        job.setJobType(jobTypeRepository.findById(request.getJobTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_TYPE_NOT_EXISTED)));
        job.setStatus(jobStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_STATUS_NOT_EXISTED)));
        job.setPriority(priorityRepository.findById(request.getPriorityId())
                .orElseThrow(() -> new AppException(ErrorCode.PRIORITY_NOT_EXISTED)));
        
        if (request.getExperienceLevelId() != null) {
            job.setExperienceLevel(experienceLevelRepository.findById(request.getExperienceLevelId())
                    .orElseThrow(() -> new AppException(ErrorCode.EXPERIENCE_LEVEL_NOT_EXISTED)));
        }
        
        return jobMapper.toJobResponse(jobRepository.save(job));
    }

    @Override
    public JobResponse getById(String id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));
        return jobMapper.toJobResponse(job);
    }

    @Override
    public Page<JobResponse> getAll(Pageable pageable) {
        return jobRepository.findAll(pageable).map(jobMapper::toJobResponse);
    }

    @Override
    @Transactional
    public JobUpdateResponse update(String id, JobUpdateRequest request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        jobMapper.updateJob(job, request);

        if (request.getStatus() != null) {
            job.setStatus(jobStatusRepository.findByName(request.getStatus())
                    .orElseThrow(() -> new AppException(ErrorCode.JOB_STATUS_NOT_EXISTED)));
        }

        return jobMapper.toJobUpdateResponse(jobRepository.save(job));
    }

    @Override
    public JobUpdateStatusResponse updateStatus(String id, JobUpdateStatusRequest request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        jobMapper.updateStatusJob(job,request);

        if(request.getStatus() != null){
            job.setStatus(jobStatusRepository.findByName(request.getStatus())
                    .orElseThrow(()-> new AppException(ErrorCode.JOB_STATUS_NOT_EXISTED)));
        }

        return jobMapper.toJobUpdateStatusResponse(jobRepository.save(job));
    }

    @Override
    public List<JobSkillResponse> getJobSkills(String jobId) {
        return jobSkillRepository.findByJobIdWithSkill(jobId)
                .stream().map(jobSkillMapper::toJobSkillResponse).toList();
    }

    @Override
    @Transactional
    public void delete(String id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        job.softDelete();
        jobRepository.save(job);
    }
}




