package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.AuditLogRequest;
import com.jobtracker.jobtracker_app.dto.responses.AuditLogResponse;
import com.jobtracker.jobtracker_app.entities.AuditLog;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    @Mapping(target = "user", ignore = true)
    AuditLog toAuditLog(AuditLogRequest request);

    @Mapping(source = "user.id", target = "userId")
    AuditLogResponse toAuditLogResponse(AuditLog auditLog);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void updateAuditLog(@MappingTarget AuditLog auditLog, AuditLogRequest request);
}




