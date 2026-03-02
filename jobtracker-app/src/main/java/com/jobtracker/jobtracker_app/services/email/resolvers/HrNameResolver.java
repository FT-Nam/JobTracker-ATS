package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HrNameResolver implements VariableResolver {

    private final UserRepository userRepository;

    @Override
    public String getKey() {
        return SystemVariable.HR_NAME.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getUserId() == null) return "";
        return userRepository.findById(context.getUserId())
                .map(u -> nullToEmpty(u.getFirstName()) + " " + nullToEmpty(u.getLastName()))
                .map(String::trim)
                .orElse("");
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
