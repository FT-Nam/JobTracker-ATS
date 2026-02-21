package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.company.CompanyFilterRequest;
import com.jobtracker.jobtracker_app.dto.requests.company.CompanyUpdateRequest;
import com.jobtracker.jobtracker_app.dto.requests.company.CompanyVerifyRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.CompanyResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.services.CompanyService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {
    CompanyService companyService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<CompanyResponse>> getAll(
            @ModelAttribute CompanyFilterRequest request,
            Pageable pageable) {
        Page<CompanyResponse> companies = companyService.getAll(request, pageable);
        return ApiResponse.<List<CompanyResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_LIST_SUCCESS))
                .data(companies.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(companies.getNumber())
                        .size(companies.getSize())
                        .totalElements(companies.getTotalElements())
                        .totalPages(companies.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CompanyResponse> getById(@PathVariable String id){
        return ApiResponse.<CompanyResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_DETAIL_SUCCESS))
                .data(companyService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CompanyResponse> update(@PathVariable String id, @RequestBody @Valid CompanyUpdateRequest request){
        return ApiResponse.<CompanyResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_UPDATE_SUCCESS))
                .data(companyService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id){
        companyService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_DELETE_SUCCESS))
                .build();
    }

    @PatchMapping("/{id}/verify")
    public ApiResponse<CompanyResponse> setVerified(
            @PathVariable String id,
            @RequestBody @Valid CompanyVerifyRequest request) {
        return ApiResponse.<CompanyResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_UPDATE_SUCCESS))
                .data(companyService.setVerified(id, request.getIsVerified()))
                .build();
    }
}
