package com.jobtracker.jobtracker_app.dto.requests.job;

import com.jobtracker.jobtracker_app.enums.JobStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobFilterRequest {
    JobStatus jobStatus;
    Boolean isRemote;
    String search;
}
