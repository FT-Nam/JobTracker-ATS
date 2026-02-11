package com.jobtracker.jobtracker_app.exceptions;

import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalException {
    LocalizationUtils localizationUtils;

    @ExceptionHandler(exception = AppException.class)
    public ResponseEntity<ApiResponse> handlingAppException(AppException e) {
        String message = localizationUtils.getLocalizedMessage(e.getErrorCode().getMessage());
        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .message(message)
                .build();
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> errors = new HashMap<>();

        for(FieldError error : e.getBindingResult().getFieldErrors()){
            errors.put(error.getField(), localizationUtils.getLocalizedMessage(error.getDefaultMessage()));
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .message(localizationUtils.getLocalizedMessage(ErrorCode.INVALID_INPUT.getMessage()))
                .errors(errors)
                .build();

        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(apiResponse);
    }

//    @ExceptionHandler(exception = DataIntegrityViolationException.class)
//    public ResponseEntity<ApiResponse> handlingDataIntegrityViolationException(
//            DataIntegrityViolationException e) {
//        Throwable cause = e.getCause();
//        if (cause instanceof ConstraintViolationException) {
//            ConstraintViolationException cve = (ConstraintViolationException) cause;
//            ErrorCode errorCode;
//            String constraint = cve.getConstraintName();
//            String sqlState = cve.getSQLException().getSQLState();
//            String fieldName = mapConstraintToField(constraint);
//            Map<String, String> errors = new HashMap<>();
//
//            switch (sqlState) {
//                case "23000":
//                    String message = localizationUtils.getLocalizedMessage(ErrorCode.FIELD_EXISTED.getMessage());
//                    errorCode = ErrorCode.INVALID_INPUT;
//                    errors.put(fieldName, message);
//                    break;
//                default:
//                    errorCode = ErrorCode.UNCATEGORIZED_ERROR;
//                    break;
//
//            }
//            return ResponseEntity
//                    .status(errorCode.getHttpStatus())
//                    .body(ApiResponse.builder()
//                            .success(false)
//                            .errors(errors)
//                            .message(localizationUtils.getLocalizedMessage(errorCode.getMessage()))
//                            .build());
//        }
//
//        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_ERROR;
//        return ResponseEntity
//                .status(errorCode.getHttpStatus())
//                .body(ApiResponse.builder()
//                        .success(false)
//                        .message(localizationUtils.getLocalizedMessage(errorCode.getMessage()))
//                        .build());
//    }
//
//    private String mapConstraintToField(String constraint) {
//        if (constraint == null) return "unknown";
//
//        if (constraint.contains("email")) return "email";
//        if (constraint.contains("username")) return "username";
//
//        return "unknown";
//    }

}
