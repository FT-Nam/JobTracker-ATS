package com.jobtracker.jobtracker_app.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.jobtracker.jobtracker_app.dto.requests.ChangePasswordRequest;
import com.jobtracker.jobtracker_app.dto.responses.user.UploadAvatarResponse;
import com.jobtracker.jobtracker_app.validator.file.FileValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.user.UserResponse;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.UserMapper;
import com.jobtracker.jobtracker_app.repositories.RoleRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.UserService;
import com.jobtracker.jobtracker_app.services.cache.PermissionCacheService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    PermissionCacheService permissionCacheService;
    PasswordEncoder passwordEncoder;
    Cloudinary cloudinary;
    FileValidator imageFileValidator;

    // ADMIN
    @Override
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @Transactional
    public UserResponse create(UserCreationRequest request) {
        User user = userMapper.toUser(request);

        boolean isUserExist = userRepository.existsByEmail(request.getEmail());
        if(isUserExist){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository
                .findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        user.setRole(role);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // ADMIN
    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserResponse getById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    // USER
    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserResponse getProfile() {
        String id = getAuthenticationId();
        User user = userRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    // ADMIN
    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public Page<UserResponse> getAll(String keyword, String roleId,
                                     Boolean isActive, Boolean emailVerified, Pageable pageable) {
        return userRepository.findAllAndSearch(keyword, roleId, isActive, emailVerified, pageable)
                .map(userMapper::toUserResponse);
    }

    // USER, ADMIN
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

    // USER
    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Transactional
    public UserResponse updateProfile(UserUpdateRequest request) {
        String id = getAuthenticationId();
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // USER
    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String id = getAuthenticationId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());

        if(!authenticated){
            throw new AppException(ErrorCode.INCORRECT_CURRENT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    // ADMIN
    @Override
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Transactional
    public void delete(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.softDelete();
        userRepository.save(user);
    }

    // ADMIN
    @Override
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Transactional
    public void restore(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.restore();
        userRepository.save(user);
    }

    // USER
    @Override
    @Transactional
    public UploadAvatarResponse uploadAvatar(MultipartFile file) throws IOException {

        imageFileValidator.validate(file);

        String userId = getAuthenticationId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String folderPath = "jobtracker_ats/user/" + userId + "/avatars";

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getInputStream(),
                ObjectUtils.asMap(
                        "folder", folderPath,
                        "resource_type", "image"
                )
        );

        String newUrl = (String) result.get("secure_url");
        String newPublicId = (String) result.get("public_id");

        if (newUrl == null || newPublicId == null) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        String oldPublicId = user.getAvatarPublicId();

        try {
            user.setAvatarUrl(newUrl);
            user.setAvatarPublicId(newPublicId);
            userRepository.save(user);
        } catch (Exception e) {
            // Rollback nếu db false
            cloudinary.uploader().destroy(newPublicId, ObjectUtils.emptyMap());
            throw e;
        }

        if (oldPublicId != null) {
            cloudinary.uploader().destroy(oldPublicId, ObjectUtils.emptyMap());
        }

        return UploadAvatarResponse.builder()
                .avatarUrl(newUrl)
                .build();
    }


    private String getAuthenticationId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
