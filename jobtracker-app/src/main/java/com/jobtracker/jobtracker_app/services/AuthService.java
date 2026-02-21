package com.jobtracker.jobtracker_app.services;

import java.text.ParseException;

import com.jobtracker.jobtracker_app.dto.requests.AuthenticationRequest;
import com.jobtracker.jobtracker_app.dto.requests.LogoutRequest;
import com.jobtracker.jobtracker_app.dto.requests.RefreshRequest;
import com.jobtracker.jobtracker_app.dto.requests.RegisterRequest;
import com.jobtracker.jobtracker_app.dto.responses.CompanySelfSignupResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.AuthenticationResponse;
import com.nimbusds.jose.JOSEException;

public interface AuthService {
    CompanySelfSignupResponse register(RegisterRequest request);

    AuthenticationResponse login(AuthenticationRequest request) throws JOSEException;

    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;

    void logout(LogoutRequest request) throws ParseException, JOSEException;
}
