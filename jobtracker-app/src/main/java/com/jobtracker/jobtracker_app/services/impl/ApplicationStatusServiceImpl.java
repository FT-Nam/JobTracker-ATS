package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.ApplicationStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApplicationStatusResponse;
import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.ApplicationStatusMapper;
import com.jobtracker.jobtracker_app.repositories.ApplicationStatusRepository;
import com.jobtracker.jobtracker_app.services.ApplicationStatusService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationStatusServiceImpl implements ApplicationStatusService {
    ApplicationStatusRepository applicationStatusRepository;
    ApplicationStatusMapper applicationStatusMapper;
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public ApplicationStatusResponse create(ApplicationStatusRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (applicationStatusRepository
                .findByNameAndCompany_IdAndDeletedAtIsNull(request.getName(), currentUser.getCompany().getId())
                .isPresent()) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }

        ApplicationStatus applicationStatus = applicationStatusMapper.toApplicationStatus(request);
        applicationStatus.setCompany(currentUser.getCompany());

        return applicationStatusMapper.toApplicationStatusResponse(
                applicationStatusRepository.save(applicationStatus));
    }

    @Override
    public ApplicationStatusResponse getById(String id) {
        User currentUser = securityUtils.getCurrentUser();

        ApplicationStatus applicationStatus = applicationStatusRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));
        return applicationStatusMapper.toApplicationStatusResponse(applicationStatus);
    }

    @Override
    public List<ApplicationStatusResponse> getAll() {
        User currentUser = securityUtils.getCurrentUser();

        return applicationStatusRepository
                .findByCompany_IdAndDeletedAtIsNull(currentUser.getCompany().getId())
                .stream()
                .map(applicationStatusMapper::toApplicationStatusResponse)
                .toList();
    }

    @Override
    @Transactional
    public ApplicationStatusResponse update(String id, ApplicationStatusRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        ApplicationStatus applicationStatus = applicationStatusRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));

        if (request.getName() != null && !request.getName().equals(applicationStatus.getName())) {
            if (applicationStatusRepository
                    .findByNameAndCompany_IdAndDeletedAtIsNull(request.getName(), currentUser.getCompany().getId())
                    .isPresent()) {
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
        User currentUser = securityUtils.getCurrentUser();

        ApplicationStatus applicationStatus = applicationStatusRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));

        applicationStatus.softDelete();
        applicationStatusRepository.save(applicationStatus);
    }
}

