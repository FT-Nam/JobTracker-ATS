package com.jobtracker.jobtracker_app.dto.requests.email;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManualOfferRequest {
    String offerSalary;
    LocalDateTime offerStartDate;
    LocalDateTime offerExpireDate;
    String customMessage;
}
