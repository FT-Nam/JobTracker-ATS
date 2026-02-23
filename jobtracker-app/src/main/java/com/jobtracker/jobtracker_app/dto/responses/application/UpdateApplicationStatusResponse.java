package com.jobtracker.jobtracker_app.dto.responses.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationStatusResponse {
    String id;
    String statusId;
    String previousStatus;
    String notes;
    LocalDateTime updatedAt;
}

