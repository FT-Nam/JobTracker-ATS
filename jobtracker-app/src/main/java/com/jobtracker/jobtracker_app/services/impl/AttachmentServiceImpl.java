package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.AttachmentRequest;
import com.jobtracker.jobtracker_app.dto.responses.AttachmentResponse;
import com.jobtracker.jobtracker_app.entities.Attachment;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.AttachmentMapper;
import com.jobtracker.jobtracker_app.repositories.AttachmentRepository;
import com.jobtracker.jobtracker_app.repositories.JobRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.AttachmentService;
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
public class AttachmentServiceImpl implements AttachmentService {
    AttachmentRepository attachmentRepository;
    AttachmentMapper attachmentMapper;
    JobRepository jobRepository;
    UserRepository userRepository;

    @Override
    @Transactional
    public AttachmentResponse create(AttachmentRequest request) {
        Attachment attachment = attachmentMapper.toAttachment(request);
        attachment.setJob(jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED)));
        attachment.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        return attachmentMapper.toAttachmentResponse(attachmentRepository.save(attachment));
    }

    @Override
    public AttachmentResponse getById(String id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTACHMENT_NOT_EXISTED));
        return attachmentMapper.toAttachmentResponse(attachment);
    }

    @Override
    public Page<AttachmentResponse> getAll(Pageable pageable) {
        return attachmentRepository.findAll(pageable).map(attachmentMapper::toAttachmentResponse);
    }

    @Override
    @Transactional
    public AttachmentResponse update(String id, AttachmentRequest request) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTACHMENT_NOT_EXISTED));

        attachmentMapper.updateAttachment(attachment, request);
        
        if (request.getJobId() != null) {
            attachment.setJob(jobRepository.findById(request.getJobId())
                    .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED)));
        }
        if (request.getUserId() != null) {
            attachment.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        }

        return attachmentMapper.toAttachmentResponse(attachmentRepository.save(attachment));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTACHMENT_NOT_EXISTED));

        attachment.softDelete();
        attachmentRepository.save(attachment);
    }
}




