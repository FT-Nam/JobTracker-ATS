package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.auth.ChangePasswordRequest;
import com.jobtracker.jobtracker_app.dto.requests.user.UserUpdateProfileRequest;
import com.jobtracker.jobtracker_app.dto.responses.user.UploadAvatarResponse;
import com.jobtracker.jobtracker_app.dto.responses.user.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    UserResponse getProfile();

    UserResponse updateProfile(UserUpdateProfileRequest request);

    void changePassword(ChangePasswordRequest request);

    UploadAvatarResponse uploadAvatar(MultipartFile file) throws IOException;
}
