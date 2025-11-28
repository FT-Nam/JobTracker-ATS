package com.jobtracker.jobtracker_app.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.InterviewStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewStatusResponse;
import com.jobtracker.jobtracker_app.entities.InterviewStatus;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.InterviewStatusMapper;
import com.jobtracker.jobtracker_app.repositories.InterviewStatusRepository;
import com.jobtracker.jobtracker_app.services.InterviewStatusService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class InterviewStatusServiceImpl implements InterviewStatusService {
    InterviewStatusRepository interviewStatusRepository;
    InterviewStatusMapper interviewStatusMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('INTERVIEW_STATUS_CREATE')")
    public InterviewStatusResponse create(InterviewStatusRequest request) {
        validateNameUnique(request.getName(), null);
        InterviewStatus interviewStatus = interviewStatusMapper.toInterviewStatus(request);
        return interviewStatusMapper.toInterviewStatusResponse(interviewStatusRepository.save(interviewStatus));
    }

    @Override
    public InterviewStatusResponse getById(String id) {
        return interviewStatusMapper.toInterviewStatusResponse(findById(id));
    }

    @Override
    public Page<InterviewStatusResponse> getAll(Pageable pageable) {
        return interviewStatusRepository.findAll(pageable).map(interviewStatusMapper::toInterviewStatusResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('INTERVIEW_STATUS_UPDATE')")
    public InterviewStatusResponse update(String id, InterviewStatusRequest request) {
        InterviewStatus interviewStatus = findById(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(interviewStatus.getName())) {
            validateNameUnique(request.getName(), id);
        }
        interviewStatusMapper.updateInterviewStatus(interviewStatus, request);
        return interviewStatusMapper.toInterviewStatusResponse(interviewStatusRepository.save(interviewStatus));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('INTERVIEW_STATUS_DELETE')")
    public void delete(String id) {
        InterviewStatus interviewStatus = findById(id);
        interviewStatus.softDelete();
        interviewStatusRepository.save(interviewStatus);
    }

    private InterviewStatus findById(String id) {
        return interviewStatusRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_STATUS_NOT_EXISTED));
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? interviewStatusRepository.existsByNameIgnoreCase(name)
                : interviewStatusRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}

