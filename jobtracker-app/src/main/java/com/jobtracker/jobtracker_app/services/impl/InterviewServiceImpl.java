package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.InterviewRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResponse;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.InterviewMapper;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.InterviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewServiceImpl implements InterviewService {
    InterviewRepository interviewRepository;
    InterviewMapper interviewMapper;
    JobRepository jobRepository;
    InterviewTypeRepository interviewTypeRepository;
    InterviewStatusRepository interviewStatusRepository;
    InterviewResultRepository interviewResultRepository;

    @Override
    @Transactional
    public InterviewResponse create(InterviewRequest request) {
        Interview interview = interviewMapper.toInterview(request);
        
        // Set relationships
        interview.setJob(jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED)));
        interview.setInterviewType(interviewTypeRepository.findById(request.getInterviewTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_TYPE_NOT_EXISTED)));
        interview.setStatus(interviewStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_STATUS_NOT_EXISTED)));
        
        if (request.getResultId() != null) {
            interview.setResult(interviewResultRepository.findById(request.getResultId())
                    .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_RESULT_NOT_EXISTED)));
        }
        
        return interviewMapper.toInterviewResponse(interviewRepository.save(interview));
    }

    @Override
    public InterviewResponse getById(String id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_EXISTED));
        return interviewMapper.toInterviewResponse(interview);
    }

    @Override
    public Page<InterviewResponse> getAll(Pageable pageable) {
        return interviewRepository.findAll(pageable).map(interviewMapper::toInterviewResponse);
    }

    @Override
    @Transactional
    public InterviewResponse update(String id, InterviewRequest request) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_EXISTED));

        interviewMapper.updateInterview(interview, request);
        
        // Update relationships if provided
        if (request.getJobId() != null) {
            interview.setJob(jobRepository.findById(request.getJobId())
                    .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED)));
        }
        if (request.getInterviewTypeId() != null) {
            interview.setInterviewType(interviewTypeRepository.findById(request.getInterviewTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_TYPE_NOT_EXISTED)));
        }
        if (request.getStatusId() != null) {
            interview.setStatus(interviewStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_STATUS_NOT_EXISTED)));
        }
        if (request.getResultId() != null) {
            interview.setResult(interviewResultRepository.findById(request.getResultId())
                    .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_RESULT_NOT_EXISTED)));
        }

        return interviewMapper.toInterviewResponse(interviewRepository.save(interview));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_EXISTED));

        interview.softDelete();
        interviewRepository.save(interview);
    }
}




