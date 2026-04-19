package com.hsyn.mapper;

import com.hsyn.model.Payment;
import com.hsyn.payload.dto.PaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Payment payment) {

        if (payment == null) {
            return null;
        }

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());

        // User info
        if(payment.getUser() != null){
            dto.setUserId(payment.getUser().getId());
            dto.setUsername(payment.getUser().getFullName());
            dto.setUserEmail(payment.getUser().getEmail());
        }

        // Book loan info
//        if (payment.getBookLoan!=null){
//            dto.setBookLoanId(payment.getBookLoan().getId());
//        }

        // Subscription info
        if (payment.getSubscription() != null) {
            dto.setSubscriptionId(payment.getSubscription().getId());
        }

        dto.setPaymentType(payment.getPaymentType());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setGateway(payment.getGateway());
        dto.setAmount(payment.getAmount());
        dto.setTransactionId(payment.getTransactionId());
        dto.setGatewayPaymentId(payment.getGatewayPaymentId());
        dto.setGatewayOrderId(payment.getGatewayOrderId());
        dto.setDescription(payment.getDescription());
        dto.setFailureReason(payment.getFailureReason());
        dto.setInitiatedAt(payment.getInitiatedAt());
        dto.setCompletedAt(payment.getCompletedAt());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());

        return dto;

    }
}
