package com.jobtracker.jobtracker_app.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.PriorityRequest;
import com.jobtracker.jobtracker_app.dto.responses.PriorityResponse;
import com.jobtracker.jobtracker_app.entities.Priority;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.PriorityMapper;
import com.jobtracker.jobtracker_app.repositories.PriorityRepository;
import com.jobtracker.jobtracker_app.services.PriorityService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class PriorityServiceImpl implements PriorityService {
    PriorityRepository priorityRepository;
    PriorityMapper priorityMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRIORITY_CREATE')")
    public PriorityResponse create(PriorityRequest request) {
        validateNameUnique(request.getName(), null);
        Priority priority = priorityMapper.toPriority(request);
        return priorityMapper.toPriorityResponse(priorityRepository.save(priority));
    }

    @Override
    public PriorityResponse getById(String id) {
        return priorityMapper.toPriorityResponse(findById(id));
    }

    @Override
    public Page<PriorityResponse> getAll(Pageable pageable) {
        return priorityRepository.findAll(pageable).map(priorityMapper::toPriorityResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRIORITY_UPDATE')")
    public PriorityResponse update(String id, PriorityRequest request) {
        Priority priority = findById(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(priority.getName())) {
            validateNameUnique(request.getName(), id);
        }
        priorityMapper.updatePriority(priority, request);
        return priorityMapper.toPriorityResponse(priorityRepository.save(priority));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRIORITY_DELETE')")
    public void delete(String id) {
        Priority priority = findById(id);
        priority.softDelete();
        priorityRepository.save(priority);
    }

    private Priority findById(String id) {
        return priorityRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRIORITY_NOT_EXISTED));
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? priorityRepository.existsByNameIgnoreCase(name)
                : priorityRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}

