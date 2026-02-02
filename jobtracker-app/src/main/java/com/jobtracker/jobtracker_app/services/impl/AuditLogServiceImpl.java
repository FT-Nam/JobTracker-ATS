package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.AuditLogRequest;
import com.jobtracker.jobtracker_app.dto.responses.AuditLogResponse;
import com.jobtracker.jobtracker_app.entities.AuditLog;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.AuditLogMapper;
import com.jobtracker.jobtracker_app.repositories.AuditLogRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.AuditLogService;
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
public class AuditLogServiceImpl implements AuditLogService {
    AuditLogRepository auditLogRepository;
    AuditLogMapper auditLogMapper;
    UserRepository userRepository;

    @Override
    @Transactional
    public AuditLogResponse create(AuditLogRequest request) {
        AuditLog auditLog = auditLogMapper.toAuditLog(request);
        
        if (request.getUserId() != null) {
            auditLog.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        }
        
        return auditLogMapper.toAuditLogResponse(auditLogRepository.save(auditLog));
    }

    @Override
    public AuditLogResponse getById(String id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AUDIT_LOG_NOT_EXISTED));
        return auditLogMapper.toAuditLogResponse(auditLog);
    }

    @Override
    public Page<AuditLogResponse> getAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(auditLogMapper::toAuditLogResponse);
    }

    @Override
    @Transactional
    public AuditLogResponse update(String id, AuditLogRequest request) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AUDIT_LOG_NOT_EXISTED));

        auditLogMapper.updateAuditLog(auditLog, request);
        
        if (request.getUserId() != null) {
            auditLog.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        }

        return auditLogMapper.toAuditLogResponse(auditLogRepository.save(auditLog));
    }

    @Override
    @Transactional
    public void delete(String id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AUDIT_LOG_NOT_EXISTED));
        auditLogRepository.delete(auditLog);
    }
}




