package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {

    @NotBlank
    String companyId;

    @NotBlank
    String companySubscriptionId;

    @NotNull
    @Min(0)
    BigDecimal amount;

    String currency;

    String gateway;

    String txnRef;
}


