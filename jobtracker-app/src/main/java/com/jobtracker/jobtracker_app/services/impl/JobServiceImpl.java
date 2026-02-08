package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.job.*;
import com.jobtracker.jobtracker_app.dto.responses.job.*;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.enums.JobStatus;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    JobSkillRepository jobSkillRepository;
    JobSkillMapper jobSkillMapper;
    SkillRepository skillRepository;

    @Override
    @Transactional
    public JobResponse create(JobCreationRequest request) {
        Job job = jobMapper.toJob(request);
        
        job.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        job.setCompany(companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED)));
        
        return jobMapper.toJobResponse(jobRepository.save(job));
    }

    @Override
    public JobResponse getById(String id) {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));
        return jobMapper.toJobResponse(job);
    }

    @Override
    public Page<JobResponse> getAllJobByUser(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return jobRepository.findAllByUserIdAndNotDeleted(userId,pageable).map(jobMapper::toJobResponse);
    }

    @Override
    @Transactional
    public JobUpdateResponse update(String id, JobUpdateRequest request) {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        jobMapper.updateJob(job, request);

        return jobMapper.toJobUpdateResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobUpdateStatusResponse updateStatus(String id, JobUpdateStatusRequest request) {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        if(request.getJobStatus() != null){
            if(job.getJobStatus() == JobStatus.DRAFT && request.getJobStatus() == JobStatus.PUBLISHED){
                job.setJobStatus(JobStatus.PUBLISHED);
                job.setPublishedAt(LocalDateTime.now());
            }
            else if(job.getJobStatus() == JobStatus.PUBLISHED && request.getJobStatus() == JobStatus.DRAFT){
                job.setJobStatus(JobStatus.DRAFT);
            }
        }

        jobRepository.save(job);

        return JobUpdateStatusResponse.builder()
                .jobStatus(job.getJobStatus())
                .publishedAt(job.getPublishedAt())
                .expiresAt(job.getExpiresAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void delete(String id) {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        job.softDelete();
        jobRepository.save(job);
    }

    @Override
    public List<JobSkillResponse> getJobSkills(String jobId) {
        return jobSkillRepository.findByJobIdWithSkill(jobId)
                .stream().map(jobSkillMapper::toJobSkillResponse).toList();
    }

    @Override
    @Transactional
    public JobSkillCreationResponse addSkillToJob(JobSkillCreationRequest request, String jobId) {
        Skill skill = skillRepository.findByIdAndDeletedAtIsNull(request.getSkillId())
                .filter(Skill::getIsActive)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(()-> new AppException(ErrorCode.JOB_NOT_EXISTED));

        if(jobSkillRepository.existsByJobAndSkill(job,skill)){
            throw new AppException(ErrorCode.JOB_SKILL_EXISTED);
        }

        JobSkill jobSkill = jobSkillMapper.toJobSkill(request);
        jobSkill.setSkill(skill);
        jobSkill.setJob(job);

        return jobSkillMapper.toJobSkillCreationResponse(jobSkillRepository.save(jobSkill));
    }

    @Override
    @Transactional
    public JobSkillResponse updateJobSkill(String jobId, String skillId, JobSkillUpdateRequest request) {
        Skill skill = skillRepository.findByIdAndDeletedAtIsNull(skillId)
                .filter(Skill::getIsActive)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(()-> new AppException(ErrorCode.JOB_NOT_EXISTED));

        JobSkill jobSkill = jobSkillRepository.findByJobAndSkill(job, skill)
                .orElseThrow(()-> new AppException(ErrorCode.JOB_SKILL_NOT_EXISTED));

        if(request.getIsRequired() != null){
            jobSkill.setIsRequired(request.getIsRequired());
        }

        if(request.getProficiencyLevel() != null && !request.getProficiencyLevel().isBlank()){
            jobSkill.setProficiencyLevel(request.getProficiencyLevel());
        }

        jobSkillRepository.save(jobSkill);

        return jobSkillMapper.toJobSkillResponse(jobSkill);
    }

    @Override
    @Transactional
    public void deleteJobSkill(String jobId, String skillId) {
        Skill skill = skillRepository.findByIdAndDeletedAtIsNull(skillId)
                .filter(Skill::getIsActive)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(()-> new AppException(ErrorCode.JOB_NOT_EXISTED));

        if(!jobSkillRepository.existsByJobAndSkill(job,skill)){
            throw new AppException(ErrorCode.JOB_SKILL_NOT_EXISTED);
        }

        JobSkill jobSkill = jobSkillRepository.findByJobAndSkill(job,skill)
                        .orElseThrow(()-> new AppException(ErrorCode.JOB_SKILL_NOT_EXISTED));

        jobSkill.softDelete();
        jobSkillRepository.save(jobSkill);
    }
}




