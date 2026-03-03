package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.repositories.InterviewRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeetingLinkResolver implements VariableResolver {

    InterviewRepository interviewRepository;

    @Override
    public String getKey() {
        return SystemVariable.MEETING_LINK.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getInterviewId() == null || context.getCompanyId() == null) return "";
        return interviewRepository.findByIdAndCompany_IdAndDeletedAtIsNull(context.getInterviewId(), context.getCompanyId())
                .map(Interview::getMeetingLink)
                .orElse("");
    }
}
