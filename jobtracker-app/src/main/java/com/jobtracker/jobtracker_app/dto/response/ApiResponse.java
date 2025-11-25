package com.jobtracker.jobtracker_app.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    Boolean success = true;

    String message;
    T data;
    Map<String, String> errors;

    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();

    PaginationInfo paginationInfo;
}
