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
public class AssignApplicationResponse {
    String id;
    String assignedTo;
    String assignedToName;
    LocalDateTime updatedAt;
}
