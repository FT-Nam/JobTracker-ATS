package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CandidateNameResolver implements VariableResolver {

    ApplicationRepository applicationRepository;

    @Override
    public String getKey() {
        return SystemVariable.CANDIDATE_NAME.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getApplicationId() == null || context.getCompanyId() == null) return "";
        return applicationRepository.findByIdAndCompany_IdAndDeletedAtIsNull(context.getApplicationId(), context.getCompanyId())
                .map(Application::getCandidateName)
                .orElse("");
    }
}
