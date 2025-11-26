package com.jobtracker.jobtracker_app.exceptions;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_ERROR("Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("Invalid input", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED("User not existed", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED("Role not existed", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_EXISTED("Permission not existed", HttpStatus.NOT_FOUND),
    FIELD_EXISTED("Field existed", HttpStatus.BAD_REQUEST),
    NAME_EXISTED("Name existed", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED("Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Invalid token", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus httpStatus;
}
