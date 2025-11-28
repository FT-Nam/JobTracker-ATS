package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobtracker.jobtracker_app.dto.requests.ExperienceLevelRequest;
import com.jobtracker.jobtracker_app.dto.responses.ExperienceLevelResponse;
import com.jobtracker.jobtracker_app.entities.ExperienceLevel;

@Mapper(componentModel = "spring")
public interface ExperienceLevelMapper {
    ExperienceLevel toExperienceLevel(ExperienceLevelRequest request);

    ExperienceLevelResponse toExperienceLevelResponse(ExperienceLevel experienceLevel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExperienceLevel(
            @MappingTarget ExperienceLevel experienceLevel, ExperienceLevelRequest request);
}

