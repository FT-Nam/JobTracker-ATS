package com.jobtracker.jobtracker_app.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.InterviewResultRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResultResponse;
import com.jobtracker.jobtracker_app.entities.InterviewResult;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.InterviewResultMapper;
import com.jobtracker.jobtracker_app.repositories.InterviewResultRepository;
import com.jobtracker.jobtracker_app.services.InterviewResultService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class InterviewResultServiceImpl implements InterviewResultService {
    InterviewResultRepository interviewResultRepository;
    InterviewResultMapper interviewResultMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('INTERVIEW_RESULT_CREATE')")
    public InterviewResultResponse create(InterviewResultRequest request) {
        validateNameUnique(request.getName(), null);
        InterviewResult interviewResult = interviewResultMapper.toInterviewResult(request);
        return interviewResultMapper.toInterviewResultResponse(interviewResultRepository.save(interviewResult));
    }

    @Override
    public InterviewResultResponse getById(String id) {
        return interviewResultMapper.toInterviewResultResponse(findById(id));
    }

    @Override
    public Page<InterviewResultResponse> getAll(Pageable pageable) {
        return interviewResultRepository.findAll(pageable).map(interviewResultMapper::toInterviewResultResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('INTERVIEW_RESULT_UPDATE')")
    public InterviewResultResponse update(String id, InterviewResultRequest request) {
        InterviewResult interviewResult = findById(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(interviewResult.getName())) {
            validateNameUnique(request.getName(), id);
        }
        interviewResultMapper.updateInterviewResult(interviewResult, request);
        return interviewResultMapper.toInterviewResultResponse(interviewResultRepository.save(interviewResult));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('INTERVIEW_RESULT_DELETE')")
    public void delete(String id) {
        InterviewResult interviewResult = findById(id);
        interviewResult.softDelete();
        interviewResultRepository.save(interviewResult);
    }

    private InterviewResult findById(String id) {
        return interviewResultRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_RESULT_NOT_EXISTED));
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? interviewResultRepository.existsByNameIgnoreCase(name)
                : interviewResultRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}

