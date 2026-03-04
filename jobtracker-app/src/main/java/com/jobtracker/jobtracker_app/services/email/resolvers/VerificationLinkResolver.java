package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationLinkResolver implements VariableResolver {

    @Value("${app.base-url:https://app.yourats.com}")
    String appBaseUrl;

    @Override
    public String getKey() {
        return SystemVariable.VERIFICATION_LINK.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context == null || context.getVerificationToken() == null || context.getVerificationToken().isBlank()) {
            return "";
        }
        return appBaseUrl + "/verify-email?token=" + context.getVerificationToken();
    }
}
