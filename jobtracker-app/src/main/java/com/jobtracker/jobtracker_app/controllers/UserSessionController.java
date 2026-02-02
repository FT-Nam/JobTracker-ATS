package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.UserSessionRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.UserSessionResponse;
import com.jobtracker.jobtracker_app.services.UserSessionService;
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
@RequestMapping("/user-sessions")
public class UserSessionController {
    UserSessionService userSessionService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<UserSessionResponse> create(@RequestBody @Valid UserSessionRequest request) {
        return ApiResponse.<UserSessionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_SESSION_CREATE_SUCCESS))
                .data(userSessionService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserSessionResponse>> getAll(Pageable pageable) {
        Page<UserSessionResponse> userSessions = userSessionService.getAll(pageable);
        return ApiResponse.<List<UserSessionResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_SESSION_LIST_SUCCESS))
                .data(userSessions.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(userSessions.getNumber())
                        .size(userSessions.getSize())
                        .totalElements(userSessions.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserSessionResponse> getById(@PathVariable String id) {
        return ApiResponse.<UserSessionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_SESSION_DETAIL_SUCCESS))
                .data(userSessionService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserSessionResponse> update(@PathVariable String id, @RequestBody @Valid UserSessionRequest request) {
        return ApiResponse.<UserSessionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_SESSION_UPDATE_SUCCESS))
                .data(userSessionService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        userSessionService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_SESSION_DELETE_SUCCESS))
                .build();
    }
}




