package com.jobtracker.jobtracker_app.exceptions;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(exception = AppException.class)
    public ResponseEntity<ApiResponse> handlingAppException(AppException e) {
        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .message(e.getErrorCode().getMessage())
                .build();
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(exception = DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handlingDataIntegrityViolationException(
            DataIntegrityViolationException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) cause;
            ErrorCode errorCode;
            String constraint = cve.getConstraintName();
            String sqlState = cve.getSQLException().getSQLState();
            String fieldName = mapConstraintToField(constraint);
            Map<String, String> errors = new HashMap<>();

            switch (sqlState) {
                case "23000":
                    errorCode = ErrorCode.INVALID_INPUT;
                    errors.put(fieldName, ErrorCode.FIELD_EXISTED.getMessage());
                    break;
                default:
                    errorCode = ErrorCode.UNCATEGORIZED_ERROR;
                    break;

            }
            return ResponseEntity
                    .status(errorCode.getHttpStatus())
                    .body(ApiResponse.builder()
                            .success(false)
                            .errors(errors)
                            .message(errorCode.getMessage())
                            .build());
        }

        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_ERROR;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.builder()
                        .success(false)
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> errors = new HashMap<>();

        for(FieldError error : e.getBindingResult().getFieldErrors()){
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .message(ErrorCode.INVALID_INPUT.getMessage())
                .errors(errors)
                .build();

        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(apiResponse);
    }

    private String mapConstraintToField(String constraint) {
        if (constraint == null) return "unknown";

        if (constraint.contains("email")) return "email";
        if (constraint.contains("username")) return "username";

        return "unknown";
    }

}
