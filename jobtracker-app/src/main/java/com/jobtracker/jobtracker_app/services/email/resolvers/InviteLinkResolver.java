package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailContext;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InviteLinkResolver implements VariableResolver {

    @Value("${app.base-url:http://localhost:5173/app}")
    String appBaseUrl;

    @Override
    public String getKey() {
        return SystemVariable.INVITE_LINK.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context == null || context.getInviteToken() == null || context.getInviteToken().isBlank()) {
            return "";
        }
        return appBaseUrl + "/accept-invite?token=" + context.getInviteToken();
    }
}
