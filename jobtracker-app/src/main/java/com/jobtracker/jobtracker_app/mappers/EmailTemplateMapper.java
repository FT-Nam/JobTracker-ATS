package com.jobtracker.jobtracker_app.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.dto.requests.email.EmailTemplateCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.email.EmailTemplateUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailTemplateDetailResponse;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailTemplateResponse;
import com.jobtracker.jobtracker_app.entities.EmailTemplate;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmailTemplateMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(target = "variables", ignore = true)
    EmailTemplateResponse toResponse(EmailTemplate source, @Context ObjectMapper objectMapper);

    // JSON TO OBJECT
    @AfterMapping
    default void setVariablesToResponse(@MappingTarget EmailTemplateResponse target, EmailTemplate source,
                                        @Context ObjectMapper objectMapper) {
        target.setVariables(parseVariables(source, objectMapper));
    }

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(target = "variables", ignore = true)
    EmailTemplateDetailResponse toDetailResponse(EmailTemplate source, @Context ObjectMapper objectMapper);

    @AfterMapping
    default void setVariablesToDetailResponse(@MappingTarget EmailTemplateDetailResponse target, EmailTemplate source,
                                              @Context ObjectMapper objectMapper) {
        target.setVariables(parseVariables(source, objectMapper));
    }

    default List<String> parseVariables(EmailTemplate source, ObjectMapper objectMapper) {
        if (source.getVariables() == null || source.getVariables().isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(source.getVariables(), new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "variables", ignore = true)
    EmailTemplate toEntity(EmailTemplateCreationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "variables", ignore = true)
    void updateEntity(@MappingTarget EmailTemplate template, EmailTemplateUpdateRequest request);
}
