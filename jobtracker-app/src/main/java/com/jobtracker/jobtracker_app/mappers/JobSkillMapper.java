package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.responses.JobSkillResponse;
import com.jobtracker.jobtracker_app.entities.JobSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobSkillMapper {
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.name", target = "name")
    @Mapping(source = "skill.category", target = "category")
    @Mapping(source = "isRequired", target = "isRequired")
    @Mapping(source = "proficiencyLevel", target = "proficiencyLevel")
    JobSkillResponse toJobSkillResponse(JobSkill jobSkill);

    List<JobSkillResponse> toJobSkillResponseList(List<JobSkill> jobSkills);
}
