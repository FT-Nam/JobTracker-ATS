package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.ApplicationStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApplicationStatusResponse;
import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.ApplicationStatusMapper;
import com.jobtracker.jobtracker_app.repositories.ApplicationStatusRepository;
import com.jobtracker.jobtracker_app.services.ApplicationStatusService;
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
public class ApplicationStatusServiceImpl implements ApplicationStatusService {
    ApplicationStatusRepository applicationStatusRepository;
    ApplicationStatusMapper applicationStatusMapper;

    @Override
    @Transactional
    public ApplicationStatusResponse create(ApplicationStatusRequest request) {
        if (applicationStatusRepository.findByNameAndDeletedAtIsNull(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }

        ApplicationStatus applicationStatus = applicationStatusMapper.toApplicationStatus(request);
        return applicationStatusMapper.toApplicationStatusResponse(
                applicationStatusRepository.save(applicationStatus));
    }

    @Override
    public ApplicationStatusResponse getById(String id) {
        ApplicationStatus applicationStatus = applicationStatusRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));
        return applicationStatusMapper.toApplicationStatusResponse(applicationStatus);
    }

    @Override
    public Page<ApplicationStatusResponse> getAll(Pageable pageable) {
        return applicationStatusRepository.findAll(pageable)
                .map(applicationStatusMapper::toApplicationStatusResponse);
    }

    @Override
    @Transactional
    public ApplicationStatusResponse update(String id, ApplicationStatusRequest request) {
        ApplicationStatus applicationStatus = applicationStatusRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));

        if (request.getName() != null && !request.getName().equals(applicationStatus.getName())) {
            if (applicationStatusRepository.findByNameAndDeletedAtIsNull(request.getName()).isPresent()) {
                throw new AppException(ErrorCode.NAME_EXISTED);
            }
        }

        applicationStatusMapper.updateApplicationStatus(applicationStatus, request);
        return applicationStatusMapper.toApplicationStatusResponse(
                applicationStatusRepository.save(applicationStatus));
    }

    @Override
    @Transactional
    public void delete(String id) {
        ApplicationStatus applicationStatus = applicationStatusRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));

        applicationStatus.softDelete();
        applicationStatusRepository.save(applicationStatus);
    }
}

