package com.jobtracker.jobtracker_app.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.InterviewTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewTypeResponse;
import com.jobtracker.jobtracker_app.entities.InterviewType;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.InterviewTypeMapper;
import com.jobtracker.jobtracker_app.repositories.InterviewTypeRepository;
import com.jobtracker.jobtracker_app.services.InterviewTypeService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class InterviewTypeServiceImpl implements InterviewTypeService {
    InterviewTypeRepository interviewTypeRepository;
    InterviewTypeMapper interviewTypeMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('INTERVIEW_TYPE_CREATE')")
    public InterviewTypeResponse create(InterviewTypeRequest request) {
        validateNameUnique(request.getName(), null);
        InterviewType interviewType = interviewTypeMapper.toInterviewType(request);
        return interviewTypeMapper.toInterviewTypeResponse(interviewTypeRepository.save(interviewType));
    }

    @Override
    public InterviewTypeResponse getById(String id) {
        return interviewTypeMapper.toInterviewTypeResponse(findById(id));
    }

    @Override
    public Page<InterviewTypeResponse> getAll(Pageable pageable) {
        return interviewTypeRepository.findAll(pageable).map(interviewTypeMapper::toInterviewTypeResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('INTERVIEW_TYPE_UPDATE')")
    public InterviewTypeResponse update(String id, InterviewTypeRequest request) {
        InterviewType interviewType = findById(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(interviewType.getName())) {
            validateNameUnique(request.getName(), id);
        }
        interviewTypeMapper.updateInterviewType(interviewType, request);
        return interviewTypeMapper.toInterviewTypeResponse(interviewTypeRepository.save(interviewType));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('INTERVIEW_TYPE_DELETE')")
    public void delete(String id) {
        InterviewType interviewType = findById(id);
        interviewType.softDelete();
        interviewTypeRepository.save(interviewType);
    }

    private InterviewType findById(String id) {
        return interviewTypeRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_TYPE_NOT_EXISTED));
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? interviewTypeRepository.existsByNameIgnoreCase(name)
                : interviewTypeRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}

