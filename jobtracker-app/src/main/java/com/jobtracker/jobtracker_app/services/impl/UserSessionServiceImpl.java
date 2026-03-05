package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.UserSessionRequest;
import com.jobtracker.jobtracker_app.dto.responses.UserSessionResponse;
import com.jobtracker.jobtracker_app.entities.UserSession;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.UserSessionMapper;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.repositories.UserSessionRepository;
import com.jobtracker.jobtracker_app.services.UserSessionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSessionServiceImpl implements UserSessionService {
    UserSessionRepository userSessionRepository;
    UserSessionMapper userSessionMapper;
    UserRepository userRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public UserSessionResponse create(UserSessionRequest request) {
        UserSession userSession = userSessionMapper.toUserSession(request);
        userSession.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        return userSessionMapper.toUserSessionResponse(userSessionRepository.save(userSession));
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserSessionResponse getById(String id) {
        UserSession userSession = userSessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_SESSION_NOT_EXISTED));
        return userSessionMapper.toUserSessionResponse(userSession);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public Page<UserSessionResponse> getAll(Pageable pageable) {
        return userSessionRepository.findAll(pageable).map(userSessionMapper::toUserSessionResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserSessionResponse update(String id, UserSessionRequest request) {
        UserSession userSession = userSessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_SESSION_NOT_EXISTED));

        userSessionMapper.updateUserSession(userSession, request);
        
        if (request.getUserId() != null) {
            userSession.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        }

        return userSessionMapper.toUserSessionResponse(userSessionRepository.save(userSession));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public void delete(String id) {
        UserSession userSession = userSessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_SESSION_NOT_EXISTED));
        userSessionRepository.delete(userSession);
    }
}




