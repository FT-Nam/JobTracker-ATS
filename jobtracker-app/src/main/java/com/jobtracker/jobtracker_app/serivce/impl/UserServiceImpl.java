package com.jobtracker.jobtracker_app.serivce.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.request.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.request.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.response.UserResponse;
import com.jobtracker.jobtracker_app.entity.Role;
import com.jobtracker.jobtracker_app.entity.User;
import com.jobtracker.jobtracker_app.exception.AppException;
import com.jobtracker.jobtracker_app.exception.ErrorCode;
import com.jobtracker.jobtracker_app.mapper.UserMapper;
import com.jobtracker.jobtracker_app.repository.RoleRepository;
import com.jobtracker.jobtracker_app.repository.UserRepository;
import com.jobtracker.jobtracker_app.serivce.UserService;
import com.jobtracker.jobtracker_app.serivce.cache.PermissionCacheService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    PermissionCacheService permissionCacheService;

    @Override
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @Transactional
    public UserResponse create(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = userMapper.toUser(request);

        Role role = roleRepository
                .findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        user.setRole(role);

        user.setEmail(request.getEmail());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserResponse getById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Transactional
    public UserResponse update(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getRoleId() != null) {
            Role role = roleRepository
                    .findById(request.getRoleId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
            user.setRole(role);

            permissionCacheService.evict(user.getId());
        }

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Transactional
    public void delete(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.softDelete();
        userRepository.save(user);
    }
}
