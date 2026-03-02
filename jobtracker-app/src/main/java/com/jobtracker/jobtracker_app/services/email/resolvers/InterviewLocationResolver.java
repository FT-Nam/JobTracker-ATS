package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.repositories.InterviewRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewLocationResolver implements VariableResolver {

    private final InterviewRepository interviewRepository;

    @Override
    public String getKey() {
        return SystemVariable.INTERVIEW_LOCATION.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getInterviewId() == null) return "";
        return interviewRepository.findById(context.getInterviewId())
                .map(Interview::getLocation)
                .orElse("");
    }
}
