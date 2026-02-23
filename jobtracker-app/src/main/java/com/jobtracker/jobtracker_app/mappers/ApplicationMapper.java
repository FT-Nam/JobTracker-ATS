package com.jobtracker.jobtracker_app.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.dto.requests.application.ApplicationUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.application.ApplicationResponse;
import com.jobtracker.jobtracker_app.dto.responses.application.ApplicationScoringResult;
import com.jobtracker.jobtracker_app.dto.responses.application.ApplicationStatusDetail;
import com.jobtracker.jobtracker_app.dto.responses.application.MatchedSkillsJson;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobTitle", source = "job.title")
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "assignedTo", source = "assignedTo.id")
    @Mapping(target = "assignedToName", source = "assignedTo.lastName")
    @Mapping(target = "matchScoreDetails", ignore = true)
    ApplicationResponse toApplicationResponse(Application application, @Context ObjectMapper objectMapper);


    // Gọi sau khi map xong tất cả các field thông thường
    // @Context là cách truyền thêm tham số vào mapper mà không map nó vào field nào cả
    // Parse JSON to Object để tính count và build matchScoreDetails
    @AfterMapping
    default void setMatchScoreDetails(@MappingTarget ApplicationResponse target, Application application,
                                     @Context ObjectMapper objectMapper) {
        if (application.getMatchScore() == null || application.getMatchedSkills() == null) {
            return;
        }
        try {
            MatchedSkillsJson matched = objectMapper.readValue(application.getMatchedSkills(), MatchedSkillsJson.class);
            int matchedRequiredCount = matched.getMatchedRequired() != null ? matched.getMatchedRequired().size() : 0;
            int totalRequiredCount = matchedRequiredCount
                    + (matched.getMissingRequired() != null ? matched.getMissingRequired().size() : 0);
            int matchedOptionalCount = matched.getMatchedOptional() != null ? matched.getMatchedOptional().size() : 0;
            int totalOptionalCount = matchedOptionalCount
                    + (matched.getMissingOptional() != null ? matched.getMissingOptional().size() : 0);

            ApplicationScoringResult scoringResult = ApplicationScoringResult.builder()
                    .matchScore(application.getMatchScore())
                    .matchedRequiredCount(matchedRequiredCount)
                    .totalRequiredCount(totalRequiredCount)
                    .matchedOptionalCount(matchedOptionalCount)
                    .totalOptionalCount(totalOptionalCount)
                    .matchedRequiredSkills(matched.getMatchedRequired())
                    .missingRequiredSkills(matched.getMissingRequired())
                    .matchedOptionalSkills(matched.getMatchedOptional())
                    .missingOptionalSkills(matched.getMissingOptional())
                    .build();
            target.setMatchScoreDetails(scoringResult);
        } catch (Exception ignored) {
        }
    }

    ApplicationStatusDetail toStatusDetail(ApplicationStatus status);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateApplication(@MappingTarget Application application, ApplicationUpdateRequest request);
}

