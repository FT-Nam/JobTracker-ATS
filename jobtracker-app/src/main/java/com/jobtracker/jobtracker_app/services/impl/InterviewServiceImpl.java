package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResponse;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    InterviewInterviewerRepository interviewInterviewerRepository;

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

        userRepository.findForUpdate(interviewers, companyId);

        validateScheduleConflict(
                request.getInterviewerIds(),
                request.getScheduledDate(),
                request.getDurationMinutes(),
                companyId,
                null
        );

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
        interview.setStatus(InterviewStatus.SCHEDULED);

        Interview saved = interviewRepository.save(interview);

        return interviewMapper.toInterviewResponse(saved);
    }

    @Override
    public InterviewResponse getById(String id) {
        User currentUser = securityUtils.getCurrentUser();
        Interview interview = interviewRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
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

        User currentUser = securityUtils.getCurrentUser();

        Interview interview = interviewRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(
                        id,
                        currentUser.getCompany().getId()
                )
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_EXISTED));

        String companyId = interview.getCompany().getId();

        LocalDateTime oldScheduledDate = interview.getScheduledDate();
        Integer oldDuration = interview.getDurationMinutes();

        LocalDateTime finalScheduledDate =
                request.getScheduledDate() != null
                        ? request.getScheduledDate()
                        : oldScheduledDate;

        Integer finalDuration =
                request.getDurationMinutes() != null
                        ? request.getDurationMinutes()
                        : oldDuration;

        Set<String> finalInterviewerIds =
                request.getInterviewerIds() != null && !request.getInterviewerIds().isEmpty()
                        ? request.getInterviewerIds()
                        : interview.getInterviewers()
                        .stream()
                        .map(ii -> ii.getInterviewer().getId())
                        .collect(Collectors.toSet());

        boolean scheduleChanged =
                request.getScheduledDate() != null
                        && !request.getScheduledDate().equals(oldScheduledDate);

        boolean durationChanged =
                request.getDurationMinutes() != null
                        && !request.getDurationMinutes().equals(oldDuration);

        boolean interviewerChanged =
                request.getInterviewerIds() != null;

        if (scheduleChanged || durationChanged || interviewerChanged) {
            userRepository.findForUpdate(finalInterviewerIds, companyId);

            validateScheduleConflict(
                    finalInterviewerIds,
                    finalScheduledDate,
                    finalDuration,
                    companyId,
                    interview.getId() // exclude chính nó
            );
        }

        if (request.getInterviewerIds() != null && !request.getInterviewerIds().isEmpty()) {

            String primaryId = request.getPrimaryInterviewerId();
            if (primaryId == null) {
                primaryId = request.getInterviewerIds().iterator().next();
            }

            Set<InterviewInterviewer> newInterviewers = new HashSet<>();

            for (String interviewerId : request.getInterviewerIds()) {

                User user = userRepository
                        .findByIdAndCompany_IdAndDeletedAtIsNull(interviewerId, companyId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                newInterviewers.add(
                        InterviewInterviewer.builder()
                                .company(interview.getCompany())
                                .interviewer(user)
                                .interview(interview)
                                .isPrimary(interviewerId.equals(primaryId))
                                .build()
                );
            }

            interview.getInterviewers().clear();
            interview.getInterviewers().addAll(newInterviewers);
        }

        interviewMapper.updateInterview(interview, request);

        if (request.getActualDate() != null) {
            interview.setStatus(InterviewStatus.COMPLETED);
        } else if (scheduleChanged || durationChanged) {
            interview.setStatus(InterviewStatus.RESCHEDULED);
        }

        Interview saved = interviewRepository.save(interview);
        return interviewMapper.toInterviewResponse(saved);
    }

    @Override
    @Transactional
    public void delete(String id) {
        User currentUser = securityUtils.getCurrentUser();
        Interview interview = interviewRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_EXISTED));

        interview.softDelete();
        interviewRepository.save(interview);
    }

    @Override
    @Transactional
    public void cancel(String id) {
        User currentUser = securityUtils.getCurrentUser();
        Interview interview = interviewRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_EXISTED));

        interview.setStatus(InterviewStatus.CANCELLED);
        interviewRepository.save(interview);
    }

    @Transactional
    public void validateScheduleConflict(
            Set<String> interviewerIds,
            LocalDateTime newStart,
            Integer durationMinutes,
            String companyId,
            String excludeInterviewId // null nếu create
    ) {

        LocalDateTime newEnd = newStart.plusMinutes(durationMinutes);

        List<InterviewStatus> activeStatuses = List.of(
                InterviewStatus.SCHEDULED,
                InterviewStatus.RESCHEDULED
        );

        for (String interviewerId : interviewerIds) {

            Set<InterviewInterviewer> existingInterviews =
                    interviewInterviewerRepository
                            .findActiveInterviewsOfInterviewer(
                                    interviewerId,
                                    companyId,
                                    activeStatuses,
                                    excludeInterviewId
                            );

            for (InterviewInterviewer ii : existingInterviews) {

                Interview interview = ii.getInterview();

                LocalDateTime existingStart = interview.getScheduledDate();
                LocalDateTime existingEnd =
                        existingStart.plusMinutes(interview.getDurationMinutes());

                if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {

                    throw new AppException(ErrorCode.SCHEDULE_CONFLICT);
                }
            }
        }
    }
}




