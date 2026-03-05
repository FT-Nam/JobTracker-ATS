package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailContext;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFirstNameResolver implements VariableResolver {

    UserRepository userRepository;

    @Override
    public String getKey() {
        return SystemVariable.USER_FIRST_NAME.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context == null || context.getUserId() == null || context.getCompanyId() == null) {
            return "";
        }
        return userRepository.findByIdAndCompany_IdAndDeletedAtIsNull(context.getUserId(), context.getCompanyId())
                .map(u -> u.getFirstName() != null ? u.getFirstName() : "")
                .orElse("");
    }
}
