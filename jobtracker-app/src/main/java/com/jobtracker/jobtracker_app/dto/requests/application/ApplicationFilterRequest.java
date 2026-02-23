package com.jobtracker.jobtracker_app.dto.requests.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationFilterRequest {
    String status;
    String jobId;
    String assignedTo;
    String search;
    Integer minMatchScore;
    Integer maxMatchScore;
    String sortBy;
    String sortOrder;
}

