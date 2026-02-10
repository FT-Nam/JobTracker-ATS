package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.AttachmentRequest;
import com.jobtracker.jobtracker_app.dto.responses.AttachmentResponse;
import com.jobtracker.jobtracker_app.entities.Attachment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "application", ignore = true)
    Attachment toAttachment(AttachmentRequest request);

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "user.id", target = "userId")
    AttachmentResponse toAttachmentResponse(Attachment attachment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateAttachment(@MappingTarget Attachment attachment, AttachmentRequest request);
}




