package com.jobtracker.jobtracker_app.dto.requests.company;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyFilterRequest {
    String industry;
    String search;
}
