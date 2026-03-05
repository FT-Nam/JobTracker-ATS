package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailContext;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyNameResolver implements VariableResolver {

    CompanyRepository companyRepository;

    @Override
    public String getKey() {
        return SystemVariable.COMPANY_NAME.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getCompanyId() == null) return "";
        return companyRepository.findByIdAndDeletedAtIsNull(context.getCompanyId())
                .map(Company::getName)
                .orElse("");
    }
}
