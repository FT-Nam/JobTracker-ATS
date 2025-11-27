package com.jobtracker.jobtracker_app.exceptions;

import com.jobtracker.jobtracker_app.utils.MessageKeys;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_ERROR(MessageKeys.UNCATEGORIZED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT(MessageKeys.INVALID_INPUT, HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(MessageKeys.USER_NOT_EXISTED, HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(MessageKeys.ROLE_NOT_EXISTED, HttpStatus.NOT_FOUND),
    PERMISSION_NOT_EXISTED(MessageKeys.PERMISSION_NOT_EXISTED, HttpStatus.NOT_FOUND),
    FIELD_EXISTED(MessageKeys.FIELD_EXISTED, HttpStatus.BAD_REQUEST),
    NAME_EXISTED(MessageKeys.NAME_EXISTED, HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(MessageKeys.UNAUTHENTICATED, HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(MessageKeys.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus httpStatus;
}
