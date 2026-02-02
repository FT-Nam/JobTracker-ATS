package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.SkillRequest;
import com.jobtracker.jobtracker_app.dto.responses.SkillResponse;
import com.jobtracker.jobtracker_app.entities.Skill;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toSkill(SkillRequest request);

    SkillResponse toSkillResponse(Skill skill);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSkill(@MappingTarget Skill skill, SkillRequest request);
}




