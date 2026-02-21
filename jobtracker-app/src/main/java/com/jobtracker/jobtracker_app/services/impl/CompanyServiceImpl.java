package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.company.CompanyFilterRequest;
import com.jobtracker.jobtracker_app.dto.requests.company.CompanyUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.CompanyResponse;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.CompanyMapper;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.services.CompanyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyServiceImpl implements CompanyService {
    CompanyRepository companyRepository;
    CompanyMapper companyMapper;

    @Override
    public CompanyResponse getById(String id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));
        return companyMapper.toCompanyResponse(company);
    }

    @Override
    public Page<CompanyResponse> getAll(CompanyFilterRequest request, Pageable pageable) {
        return companyRepository.searchCompanies(
                request.getIndustry(),
                request.getSearch(),
                pageable
        ).map(companyMapper::toCompanyResponse);
    }

    @Override
    @Transactional
    public CompanyResponse update(String id, CompanyUpdateRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));
        companyMapper.updateCompany(company, request);
        return companyMapper.toCompanyResponse(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyResponse setVerified(String id, boolean isVerified) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));
        company.setIsVerified(isVerified);
        return companyMapper.toCompanyResponse(companyRepository.save(company));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));
        company.softDelete();
        companyRepository.save(company);
    }
}
