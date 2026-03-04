package com.jobtracker.jobtracker_app.services.impl;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jobtracker.jobtracker_app.dto.requests.*;
import com.jobtracker.jobtracker_app.dto.responses.*;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.mappers.UserMapper;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.responses.common.AuthenticationResponse;
import com.jobtracker.jobtracker_app.dto.responses.user.UserInfo;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.repositories.EmailVerificationTokenRepository;
import com.jobtracker.jobtracker_app.repositories.InvalidatedRepository;
import com.jobtracker.jobtracker_app.repositories.PasswordResetTokenRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.AuthService;
import com.jobtracker.jobtracker_app.services.EmailService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    RedisTemplate<String, String> redisTemplate;
    InvalidatedRepository invalidatedRepository;
    RoleRepository roleRepository;
    CompanyRepository companyRepository;
    UserMapper userMapper;
    EmailVerificationTokenRepository emailVerificationTokenRepository;
    PasswordResetTokenRepository passwordResetTokenRepository;
    EmailService emailService;

    private static final String CACHE_PREFIX = "refresh_token:";
    private static final String COMPANY_ADMIN_ROLE = "COMPANY_ADMIN";

    @NonFinal
    @Value("${jwt.signer-key}")
    String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    Long validDuration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    Long refreshableDuration;

    @NonFinal
    @Value("${auth.verification-token-expiry-hours:24}")
    int verificationTokenExpiryHours;

    @NonFinal
    @Value("${auth.password-reset-token-expiry-hours:1}")
    int passwordResetTokenExpiryHours;

    @Override
    @Transactional
    public CompanySelfSignupResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Company company = Company.builder()
                .name(request.getCompanyName())
                .isVerified(false)
                .isActive(true)
                .build();
        company = companyRepository.save(company);

        Role companyAdminRole = roleRepository.findByName(COMPANY_ADMIN_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .company(company)
                .role(companyAdminRole)
                .emailVerified(false)
                .isActive(true)
                .isBillable(true)
                .build();

        user = userRepository.save(user);

        EmailVerificationToken verificationToken = createAndSaveVerificationToken(user);
        emailService.sendEmailVerification(user, verificationToken);

        CompanyRegisterResponse companyRegisterResponse = CompanyRegisterResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .build();

        UserRegisterResponse userRegisterResponse = UserRegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roleName(user.getRole().getName())
                .emailVerified(user.getEmailVerified())
                .isActive(user.getIsActive())
                .build();

        return CompanySelfSignupResponse.builder()
                .company(companyRegisterResponse)
                .user(userRegisterResponse)
                .build();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) throws JOSEException {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        AuthenticationResponse authenticationResponse = authenticationResponse(user);

        checkAndCreateRefreshToken(user.getId(), authenticationResponse.getTokens().getRefreshToken());

        return authenticationResponse;
    }

    @Override
    @Transactional
    public EmailVerifyResponse emailVerify(EmailVerifyRequest request) {
        if (request.getToken() == null || request.getToken().isBlank()) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }
        EmailVerificationToken token = emailVerificationTokenRepository
                .findValidByToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN));

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        token.setUsedAt(LocalDateTime.now());
        emailVerificationTokenRepository.save(token);

        return EmailVerifyResponse.builder()
                .email(user.getEmail())
                .emailVerified(true)
                .build();
    }

    @Override
    @Transactional
    public void resendEmailVerify(ResendEmailVerifyRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        EmailVerificationToken token = createAndSaveVerificationToken(user);
        emailService.sendEmailVerificationResend(user, token);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJwt = verifyToken(request.getRefreshToken());
        String sub = signedJwt.getJWTClaimsSet().getSubject();

        User user = userRepository.findById(sub).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        AuthenticationResponse authenticationResponse = authenticationResponse(user);

        checkAndCreateRefreshToken(user.getId(), authenticationResponse.getTokens().getRefreshToken());

        return authenticationResponse;
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signedJwt = SignedJWT.parse(request.getAccessToken());

        String sub = signedJwt.getJWTClaimsSet().getSubject();
        String jit = signedJwt.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJwt.getJWTClaimsSet().getExpirationTime();
        String key = CACHE_PREFIX + sub;

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidatedRepository.save(invalidatedToken);
        redisTemplate.delete(key);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return;
        }
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            PasswordResetToken token = createAndSavePasswordResetToken(user);
            emailService.sendPasswordReset(user, token);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (request.getToken() == null || request.getToken().isBlank()
                || request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new AppException(ErrorCode.INVALID_RESET_TOKEN);
        }
        PasswordResetToken token = passwordResetTokenRepository
                .findValidByToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_RESET_TOKEN));

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verify = signedJWT.verify(jwsVerifier);

        if (!verify || expiryTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user, boolean isRefreshToken) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        long expirationTime = isRefreshToken ? refreshableDuration : validDuration;

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("ftnam")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(expirationTime, ChronoUnit.SECONDS)))
                .jwtID(UUID.randomUUID().toString());
        if (!isRefreshToken) {
            claimsBuilder.claim("companyId", user.getCompany().getId());
            claimsBuilder.claim("role", user.getRole().getName());
        }
        JWTClaimsSet claimsSet = claimsBuilder.build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(signerKey));

        return jwsObject.serialize();
    }

    private AuthenticationResponse authenticationResponse(User user) throws JOSEException {
        String accessToken = generateToken(user, false);
        String refreshToken = generateToken(user, true);

        TokenInfo tokenInfo = TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(Date.from(Instant.now().plusSeconds(validDuration)))
                .refreshExpiresIn(Date.from(Instant.now().plusSeconds(refreshableDuration)))
                .build();

        UserInfo userInfo = UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .roleName(user.getRole().getName())
                .companyId(user.getCompany().getId())
                .companyName(user.getCompany().getName())
                .build();

        return AuthenticationResponse.builder()
                .user(userInfo)
                .tokens(tokenInfo)
                .build();
    }

    private void checkAndCreateRefreshToken(String sub, String refreshToken) {
        String key = CACHE_PREFIX + sub;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }

        redisTemplate.opsForValue().set(key, refreshToken, refreshableDuration, TimeUnit.SECONDS);
    }


    private EmailVerificationToken createAndSaveVerificationToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .company(user.getCompany())
                .token(UUID.randomUUID().toString())
                .expiresAt(now.plusHours(VERIFICATION_TOKEN_EXPIRY_HOURS))
                .sentAt(now)
                .build();
        return emailVerificationTokenRepository.save(token);
    }

    private PasswordResetToken createAndSavePasswordResetToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .company(user.getCompany())
                .token(UUID.randomUUID().toString())
                .expiresAt(now.plusHours(passwordResetTokenExpiryHours))
                .sentAt(now)
                .build();
        return passwordResetTokenRepository.save(token);
    }
}
