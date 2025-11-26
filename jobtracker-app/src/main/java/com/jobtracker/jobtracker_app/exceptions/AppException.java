package com.jobtracker.jobtracker_app.exceptions;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppException extends RuntimeException {
    ErrorCode errorCode;
}
