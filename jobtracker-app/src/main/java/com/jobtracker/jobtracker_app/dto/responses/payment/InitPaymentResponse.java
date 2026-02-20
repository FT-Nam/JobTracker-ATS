package com.jobtracker.jobtracker_app.dto.responses.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InitPaymentResponse {
    PaymentResponse payment;
    String paymentUrl;
}
