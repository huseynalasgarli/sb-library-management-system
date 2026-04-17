package com.hsyn.payload.request;

import com.hsyn.domain.PaymentGateway;
import com.hsyn.domain.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateRequest {

    @NotNull(message = "User Id is mandatory")
    private Long userId;

    private Long bookLoanId; //Required only for fine payments

    @NotNull(message = "Payment type is mandatory")
    private PaymentType paymentType;

    @NotNull(message = "Payment gateway is mandatory")
    private PaymentGateway  paymentGateway;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private Long amount;

    @Size(max = 500,message = "Description must not exceed 500 characters")
    private String description;

    private Long fineId;
    private Long subscriptionId;

    @Size(max = 500, message = "Success URL must not exceed 500 characters")
    private String successUrl;

    @Size(max = 500, message = "Cancel URL must not exceed 500 characters")
    private String cancelUrl;

}
