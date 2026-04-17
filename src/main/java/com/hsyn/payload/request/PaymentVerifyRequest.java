package com.hsyn.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerifyRequest {
    private String stripePaymentIntentId;
    private String stripeSessionId;
    private String stripePaymentStatus;
}
