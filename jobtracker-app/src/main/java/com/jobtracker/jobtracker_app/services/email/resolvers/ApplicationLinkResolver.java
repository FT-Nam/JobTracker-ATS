package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationLinkResolver implements VariableResolver {

    private final ApplicationRepository applicationRepository;

    @Value("${app.base-url:https://app.yourats.com}")
    String appBaseUrl;

    @Override
    public String getKey() {
        return SystemVariable.APPLICATION_LINK.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getApplicationId() == null) return "";
        return applicationRepository.findByIdWithJobAndStatus(context.getApplicationId())
                .map(this::buildLink)
                .orElse("");
    }

    private String buildLink(Application app) {
        String token = app.getApplicationToken();
        if (token != null && !token.isBlank()) {
            return appBaseUrl + "/status?token=" + token;
        }
        return appBaseUrl + "/applications/" + (app.getId() != null ? app.getId() : "");
    }
}
