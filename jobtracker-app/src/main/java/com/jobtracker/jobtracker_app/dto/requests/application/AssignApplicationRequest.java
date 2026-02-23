package com.jobtracker.jobtracker_app.dto.requests.application;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignApplicationRequest {
    String assignedTo;
}
