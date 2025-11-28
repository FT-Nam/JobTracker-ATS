package com.jobtracker.jobtracker_app.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.ExperienceLevelRequest;
import com.jobtracker.jobtracker_app.dto.responses.ExperienceLevelResponse;
import com.jobtracker.jobtracker_app.entities.ExperienceLevel;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.ExperienceLevelMapper;
import com.jobtracker.jobtracker_app.repositories.ExperienceLevelRepository;
import com.jobtracker.jobtracker_app.services.ExperienceLevelService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class ExperienceLevelServiceImpl implements ExperienceLevelService {
    ExperienceLevelRepository experienceLevelRepository;
    ExperienceLevelMapper experienceLevelMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('EXPERIENCE_LEVEL_CREATE')")
    public ExperienceLevelResponse create(ExperienceLevelRequest request) {
        validateNameUnique(request.getName(), null);
        ExperienceLevel experienceLevel = experienceLevelMapper.toExperienceLevel(request);
        return experienceLevelMapper.toExperienceLevelResponse(experienceLevelRepository.save(experienceLevel));
    }

    @Override
    public ExperienceLevelResponse getById(String id) {
        return experienceLevelMapper.toExperienceLevelResponse(findById(id));
    }

    @Override
    public Page<ExperienceLevelResponse> getAll(Pageable pageable) {
        return experienceLevelRepository.findAll(pageable).map(experienceLevelMapper::toExperienceLevelResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('EXPERIENCE_LEVEL_UPDATE')")
    public ExperienceLevelResponse update(String id, ExperienceLevelRequest request) {
        ExperienceLevel experienceLevel = findById(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(experienceLevel.getName())) {
            validateNameUnique(request.getName(), id);
        }
        experienceLevelMapper.updateExperienceLevel(experienceLevel, request);
        return experienceLevelMapper.toExperienceLevelResponse(experienceLevelRepository.save(experienceLevel));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('EXPERIENCE_LEVEL_DELETE')")
    public void delete(String id) {
        ExperienceLevel experienceLevel = findById(id);
        experienceLevel.softDelete();
        experienceLevelRepository.save(experienceLevel);
    }

    private ExperienceLevel findById(String id) {
        return experienceLevelRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXPERIENCE_LEVEL_NOT_EXISTED));
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? experienceLevelRepository.existsByNameIgnoreCase(name)
                : experienceLevelRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}

