package com.jobtracker.jobtracker_app.services.impl;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.responses.UserResponse;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.mappers.UserMapper;
import com.jobtracker.jobtracker_app.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobtracker.jobtracker_app.dto.requests.AuthenticationRequest;
import com.jobtracker.jobtracker_app.dto.requests.LogoutRequest;
import com.jobtracker.jobtracker_app.dto.requests.RefreshRequest;
import com.jobtracker.jobtracker_app.dto.responses.AuthenticationResponse;
import com.jobtracker.jobtracker_app.dto.responses.TokenInfo;
import com.jobtracker.jobtracker_app.dto.responses.UserInfo;
import com.jobtracker.jobtracker_app.entities.InvalidatedToken;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.repositories.InvalidatedRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.AuthService;
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
    UserMapper userMapper;

    private static final String CACHE_PREFIX = "refresh_token:";
    private static final String DEFAULT_ROLE = "USER";

    @NonFinal
    @Value("${jwt.signer-key}")
    String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    Long validDuration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    Long refreshableDuration;

    @Override
    public UserResponse register(UserCreationRequest request) {
        User user = userMapper.toUser(request);

        Role role = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        user.setRole(role);
        user.setIsActive(true);

        return userMapper.toUserResponse(userRepository.save(user));
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

        AuthenticationResponse authenticationResponse = authenticationResponse(user);

        checkAndCreateRefreshToken(
                user.getId(), authenticationResponse.getTokens().getRefreshToken());

        return authenticationResponse;
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJwt = verifyToken(request.getRefreshToken());
        String sub = signedJwt.getJWTClaimsSet().getSubject();

        User user = userRepository.findById(sub).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        AuthenticationResponse authenticationResponse = authenticationResponse(user);

        checkAndCreateRefreshToken(
                user.getId(), authenticationResponse.getTokens().getRefreshToken());

        return authenticationResponse;
    }

    @Override
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

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("ftnam")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(expirationTime, ChronoUnit.SECONDS)))
                .jwtID(UUID.randomUUID().toString())
                .build();

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
                .build();

        return AuthenticationResponse.builder().tokens(tokenInfo).user(userInfo).build();
    }

    private void checkAndCreateRefreshToken(String sub, String refreshToken) {
        String key = CACHE_PREFIX + sub;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }

        redisTemplate.opsForValue().set(key, refreshToken, refreshableDuration, TimeUnit.SECONDS);
    }
}
