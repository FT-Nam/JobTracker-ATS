package com.jobtracker.jobtracker_app.dto.responses.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatusHistoryResponse {
    String id;
    String applicationId;
    String fromStatusId;
    StatusHistory fromStatus;
    String toStatusId;
    StatusHistory toStatus;
    String changedBy;
    String changedByName;
    String notes;
    LocalDateTime createdAt;
}
