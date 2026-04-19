package com.hsyn.payload.dto;

import com.hsyn.domain.PaymentGateway;
import com.hsyn.domain.PaymentStatus;
import com.hsyn.domain.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private Long id;

    @NotNull(message = "User Id is mandatory")
    private Long userId;

    private String username;

    private String userEmail;
    private Long bookLoanId;

    private Long subscriptionId;

    @NotNull(message = "Payment type is mandatory")
    private PaymentType paymentType;

    private PaymentStatus paymentStatus;

    @NotNull(message = "Payment gateway is mandatory")
    private PaymentGateway gateway;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private Long amount;

    private String transactionId;

    private String gatewayPaymentId;

    private String gatewayOrderId;

    private String description;

    private String failureReason;

    private Integer retryCount;

    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
