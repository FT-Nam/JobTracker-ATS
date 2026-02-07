package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.job.JobCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobSkillCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobSkillUpdateRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.job.JobResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobSkillCreationResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobSkillResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobUpdateResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobUpdateStatusResponse;
import com.jobtracker.jobtracker_app.services.JobService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class JobController {
    JobService jobService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<JobResponse> create(@RequestBody @Valid JobCreationRequest request) {
        return ApiResponse.<JobResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_CREATE_SUCCESS))
                .data(jobService.create(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<List<JobResponse>> getAll(@PathVariable String userId, Pageable pageable) {
        Page<JobResponse> jobs = jobService.getAllJobByUser(userId, pageable);
        return ApiResponse.<List<JobResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_LIST_SUCCESS))
                .data(jobs.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(jobs.getNumber())
                        .size(jobs.getSize())
                        .totalElements(jobs.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<JobResponse> getById(@PathVariable String id) {
        return ApiResponse.<JobResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_DETAIL_SUCCESS))
                .data(jobService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<JobUpdateResponse> update(@PathVariable String id, @RequestBody @Valid JobUpdateRequest request) {
        return ApiResponse.<JobUpdateResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_UPDATE_SUCCESS))
                .data(jobService.update(id, request))
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<JobUpdateStatusResponse> update(@PathVariable String id, @RequestBody @Valid JobUpdateStatusRequest request) {
        return ApiResponse.<JobUpdateStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_UPDATE_SUCCESS))
                .data(jobService.updateStatus(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        jobService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_DELETE_SUCCESS))
                .build();
    }

    // Job Skills Management
    @GetMapping("/{jobId}/skills")
    public ApiResponse<List<JobSkillResponse>> getJobSkills(@PathVariable String jobId) {
        return ApiResponse.<List<JobSkillResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_SKILL_LIST_SUCCESS))
                .data(jobService.getJobSkills(jobId))
                .build();
    }

    @PostMapping("/{jobId}/skills")
    public ApiResponse<JobSkillCreationResponse> addSkillToJob(
            @PathVariable String jobId,
            @RequestBody @Valid JobSkillCreationRequest request) {
        return ApiResponse.<JobSkillCreationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_SKILL_CREATE_SUCCESS))
                .data(jobService.addSkillToJob(request, jobId))
                .build();
    }

    @PatchMapping("/{jobId}/skills/{skillId}")
    public ApiResponse<JobSkillResponse> updateJobSkill(
            @PathVariable String jobId,
            @PathVariable String skillId,
            @RequestBody @Valid JobSkillUpdateRequest request) {
        return ApiResponse.<JobSkillResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_SKILL_UPDATE_SUCCESS))
                .data(jobService.updateJobSkill(jobId, skillId, request))
                .build();
    }

    @DeleteMapping("/{jobId}/skills/{skillId}")
    public ApiResponse<Void> deleteJobSkill(
            @PathVariable String jobId,
            @PathVariable String skillId) {
        jobService.deleteJobSkill(jobId, skillId);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_SKILL_DELETE_SUCCESS))
                .build();
    }
}




