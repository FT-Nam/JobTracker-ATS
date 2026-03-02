package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStatusResolver implements VariableResolver {

    private final ApplicationRepository applicationRepository;

    @Override
    public String getKey() {
        return SystemVariable.APPLICATION_STATUS.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getApplicationId() == null) return "";
        return applicationRepository.findByIdWithJobAndStatus(context.getApplicationId())
                .map(Application::getStatus)
                .filter(s -> s != null)
                .map(s -> s.getDisplayName() != null ? s.getDisplayName() : "")
                .orElse("");
    }
}
