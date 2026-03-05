package com.jobtracker.jobtracker_app.services;

import java.text.ParseException;

import com.jobtracker.jobtracker_app.dto.requests.auth.*;
import com.jobtracker.jobtracker_app.dto.responses.auth.AcceptInviteResponse;
import com.jobtracker.jobtracker_app.dto.responses.auth.AuthResult;
import com.jobtracker.jobtracker_app.dto.responses.company.CompanySelfSignupResponse;
import com.jobtracker.jobtracker_app.dto.responses.auth.EmailVerifyResponse;
import com.nimbusds.jose.JOSEException;

public interface AuthService {
    CompanySelfSignupResponse register(RegisterRequest request);

    AuthResult login(AuthenticationRequest request) throws JOSEException;

    EmailVerifyResponse emailVerify(EmailVerifyRequest request);

    void resendEmailVerify(ResendEmailVerifyRequest request);

    AuthResult refreshToken(String refreshToken) throws ParseException, JOSEException;

    void logout(LogoutRequest request, String refreshTokenFromCookie) throws ParseException, JOSEException;

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    AcceptInviteResponse acceptInvite(AcceptInviteRequest request);
}
