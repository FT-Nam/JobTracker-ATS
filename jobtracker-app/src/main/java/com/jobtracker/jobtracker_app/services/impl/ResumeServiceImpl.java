package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.ResumeRequest;
import com.jobtracker.jobtracker_app.dto.responses.ResumeResponse;
import com.jobtracker.jobtracker_app.entities.Resume;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.ResumeMapper;
import com.jobtracker.jobtracker_app.repositories.ResumeRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.ResumeService;
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
public class ResumeServiceImpl implements ResumeService {
    ResumeRepository resumeRepository;
    ResumeMapper resumeMapper;
    UserRepository userRepository;

    @Override
    @Transactional
    public ResumeResponse create(ResumeRequest request) {
        Resume resume = resumeMapper.toResume(request);
        resume.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        return resumeMapper.toResumeResponse(resumeRepository.save(resume));
    }

    @Override
    public ResumeResponse getById(String id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESUME_NOT_EXISTED));
        return resumeMapper.toResumeResponse(resume);
    }

    @Override
    public Page<ResumeResponse> getAll(Pageable pageable) {
        return resumeRepository.findAll(pageable).map(resumeMapper::toResumeResponse);
    }

    @Override
    @Transactional
    public ResumeResponse update(String id, ResumeRequest request) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESUME_NOT_EXISTED));

        resumeMapper.updateResume(resume, request);
        
        if (request.getUserId() != null) {
            resume.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        }

        return resumeMapper.toResumeResponse(resumeRepository.save(resume));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESUME_NOT_EXISTED));

        resume.softDelete();
        resumeRepository.save(resume);
    }
}




