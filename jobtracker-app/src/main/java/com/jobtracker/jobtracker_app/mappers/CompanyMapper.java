package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.company.CompanyCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.company.CompanyUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.CompanyResponse;
import com.jobtracker.jobtracker_app.entities.Company;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    Company toCompany(CompanyCreationRequest request);

    CompanyResponse toCompanyResponse(Company company);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCompany(@MappingTarget Company company, CompanyUpdateRequest request);
}
