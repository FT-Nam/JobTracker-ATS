package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResponse;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.InterviewMapper;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.InterviewService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewServiceImpl implements InterviewService {
    InterviewRepository interviewRepository;
    InterviewMapper interviewMapper;
    ApplicationRepository applicationRepository;
    UserRepository userRepository;
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public InterviewResponse create(InterviewCreationRequest request, String applicationId) {
        Set<InterviewInterviewer> interviewInterviewersSet = new HashSet<>();

        User currentUser = securityUtils.getCurrentUser();

        Application application = applicationRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(applicationId, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        String companyId = application.getCompany().getId();
        String primaryId = request.getPrimaryInterviewerId();

        Set<String> interviewers = request.getInterviewerIds();

        // nếu primary không set => lấy thằng đầu tiên
        if (primaryId == null && !interviewers.isEmpty()) {
            primaryId = interviewers.iterator().next();
        }

        Interview interview = interviewMapper.toInterview(request);

        for(String interviewerId  : interviewers){
            User user = userRepository.findByIdAndCompany_IdAndDeletedAtIsNull(interviewerId , companyId)
                    .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

            InterviewInterviewer interviewInterviewer = InterviewInterviewer.builder()
                    .company(application.getCompany())
                    .interviewer(user)
                    .interview(interview)
                    .isPrimary(interviewerId.equals(primaryId))
                    .build();

            interviewInterviewersSet.add(interviewInterviewer);
        }

        interview.setApplication(application);
        interview.setCompany(application.getCompany());
        interview.setJob(application.getJob());
        interview.setInterviewers(interviewInterviewersSet);

        Interview saved = interviewRepository.save(interview);

        return interviewMapper.toInterviewResponse(saved);
    }

    @Override
    public InterviewResponse getById(String id) {
        Interview interview = interviewRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_EXISTED));
        return interviewMapper.toInterviewResponse(interview);
    }

    @Override
    public List<InterviewResponse> getAll(String applicationId) {
        User currentUser = securityUtils.getCurrentUser();

        Application application = applicationRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(applicationId, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));
        return interviewRepository.findByApplicationIdWithInterviewers(application.getId())
                .stream().map(interviewMapper::toInterviewResponse).toList();
    }

    @Override
    @Transactional
    public InterviewResponse update(String id, InterviewUpdateRequest request) {
        Interview interview = interviewRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_EXISTED));

        interviewMapper.updateInterview(interview, request);

        return interviewMapper.toInterviewResponse(interviewRepository.save(interview));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Interview interview = interviewRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_EXISTED));

        interview.softDelete();
        interviewRepository.save(interview);
    }
}




