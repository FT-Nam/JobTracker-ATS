package com.jobtracker.jobtracker_app.controllers;

import java.text.ParseException;

import com.jobtracker.jobtracker_app.dto.requests.EmailVerifyRequest;
import com.jobtracker.jobtracker_app.dto.requests.ForgotPasswordRequest;
import com.jobtracker.jobtracker_app.dto.requests.RegisterRequest;
import com.jobtracker.jobtracker_app.dto.requests.ResendEmailVerifyRequest;
import com.jobtracker.jobtracker_app.dto.requests.ResetPasswordRequest;
import com.jobtracker.jobtracker_app.dto.responses.CompanySelfSignupResponse;
import com.jobtracker.jobtracker_app.dto.responses.EmailVerifyResponse;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracker.jobtracker_app.dto.requests.AuthenticationRequest;
import com.jobtracker.jobtracker_app.dto.requests.LogoutRequest;
import com.jobtracker.jobtracker_app.dto.requests.RefreshRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.AuthenticationResponse;
import com.jobtracker.jobtracker_app.services.AuthService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    AuthService authService;
    LocalizationUtils localizationUtils;

    @PostMapping("register")
    public ApiResponse<CompanySelfSignupResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.<CompanySelfSignupResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_SELF_SIGNUP_SUCCESS))
                .data(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) throws JOSEException {
        return ApiResponse.<AuthenticationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_LOGIN_SUCCESS))
                .data(authService.login(request))
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken(@RequestBody @Valid RefreshRequest request)
            throws ParseException, JOSEException {
        return ApiResponse.<AuthenticationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_REFRESH_SUCCESS))
                .data(authService.refreshToken(request))
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody @Valid LogoutRequest request) throws ParseException, JOSEException {
        authService.logout(request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_LOGOUT_SUCCESS))
                .build();
    }

    @PostMapping("/verify-email")
    ApiResponse<EmailVerifyResponse> verifyEmail(@RequestBody @Valid EmailVerifyRequest request) {
        return ApiResponse.<EmailVerifyResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_VERIFIED_SUCCESS))
                .data(authService.emailVerify(request))
                .build();
    }

    @PostMapping("/resend-verification")
    ApiResponse<Void> resendVerification(@RequestBody @Valid ResendEmailVerifyRequest request) {
        authService.resendEmailVerify(request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.VERIFICATION_EMAIL_SENT))
                .build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_RESET_EMAIL_SENT))
                .build();
    }

    @PostMapping("/reset-password")
    ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_RESET_SUCCESS))
                .build();
    }
}
