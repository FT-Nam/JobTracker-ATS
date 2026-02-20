package com.jobtracker.jobtracker_app.dto.responses.payment;

import com.jobtracker.jobtracker_app.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {

    String id;
    String companyId;
    String companySubscriptionId;
    BigDecimal amount;
    String currency;
    String gateway;
    String txnRef;
    PaymentStatus status;
    LocalDateTime paidAt;
    String metadata;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}


