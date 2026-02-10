package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.PaymentRequest;
import com.jobtracker.jobtracker_app.dto.responses.PaymentResponse;
import com.jobtracker.jobtracker_app.entities.Payment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    Payment toPayment(PaymentRequest request);

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "companySubscription.id", target = "companySubscriptionId")
    PaymentResponse toPaymentResponse(Payment payment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePayment(@MappingTarget Payment payment, PaymentRequest request);
}


