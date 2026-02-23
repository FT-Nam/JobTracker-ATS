package com.jobtracker.jobtracker_app.dto.requests.application;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationStatusUpdateRequest {

    @NotBlank(message = "{application.status_id.not_blank}")
    String statusId;

    String notes;
}

