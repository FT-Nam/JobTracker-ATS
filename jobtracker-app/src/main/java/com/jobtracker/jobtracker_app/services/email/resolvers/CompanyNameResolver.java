package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyNameResolver implements VariableResolver {

    private final CompanyRepository companyRepository;

    @Override
    public String getKey() {
        return SystemVariable.COMPANY_NAME.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getCompanyId() == null) return "";
        return companyRepository.findById(context.getCompanyId())
                .map(Company::getName)
                .orElse("");
    }
}
