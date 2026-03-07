package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailContext;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationLinkResolver implements VariableResolver {

    ApplicationRepository applicationRepository;

    @NonFinal
    @Value("${app.base-url:http://localhost:5173/app}")
    String appBaseUrl;

    @Override
    public String getKey() {
        return SystemVariable.APPLICATION_LINK.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getApplicationId() == null || context.getCompanyId() == null) return "";
        return applicationRepository.findByIdAndCompany_IdWithJobAndStatus(context.getApplicationId(), context.getCompanyId())
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
