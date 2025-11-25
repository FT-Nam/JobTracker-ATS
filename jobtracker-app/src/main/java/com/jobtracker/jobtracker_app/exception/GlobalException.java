package com.jobtracker.jobtracker_app.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jobtracker.jobtracker_app.dto.response.ApiResponse;

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
}
