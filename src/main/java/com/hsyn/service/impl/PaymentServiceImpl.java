package com.hsyn.service.impl;

import com.hsyn.domain.PaymentGateway;
import com.hsyn.domain.PaymentStatus;
import com.hsyn.exception.PaymentException;
import com.hsyn.model.Payment;
import com.hsyn.model.Subscription;
import com.hsyn.model.User;
import com.hsyn.payload.dto.PaymentDTO;
import com.hsyn.payload.request.PaymentInitiateRequest;
import com.hsyn.payload.request.PaymentVerifyRequest;
import com.hsyn.payload.response.PaymentInitiateResponse;
import com.hsyn.payload.response.PaymentLinkResponse;
import com.hsyn.repository.PaymentRepository;
import com.hsyn.repository.SubscriptionRepository;
import com.hsyn.repository.UserRepository;
import com.hsyn.service.PaymentService;
import com.hsyn.service.gateway.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {


    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;

    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest req) throws PaymentException {

        User user = userRepository.findById(req.getUserId()).get();

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentType(req.getPaymentType());
        payment.setGateway(req.getPaymentGateway());
        payment.setAmount(req.getAmount());
        payment.setDescription(req.getDescription());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setTransactionId("TXN_ " + UUID.randomUUID());
        payment.setInitiatedAt(LocalDateTime.now());

        if(req.getSubscriptionId() != null){
            Subscription sub = subscriptionRepository
                    .findById(req.getSubscriptionId())
                    .orElseThrow(()-> new PaymentException("Subscription not found"));
            payment.setSubscription(sub);
        }
        payment = paymentRepository.save(payment);

        PaymentInitiateResponse response = new PaymentInitiateResponse();

        if(req.getPaymentGateway() == PaymentGateway.STRIPE){
            PaymentLinkResponse paymentLinkResponse = stripeService.createPaymentLink(
                    user,payment
            );
            response = PaymentInitiateResponse.builder()
                    .paymentId(payment.getId())
                    .gateway(payment.getGateway())
                    .checkoutUrl(paymentLinkResponse.getPayment_link_url())
                    .transactionId(payment.getTransactionId())
                    .amount(payment.getAmount())
                    .description(payment.getDescription())
                    .success(true)
                    .message("Payment initiated successfully")
                    .build();


            payment.setGatewayOrderId(paymentLinkResponse.getPayment_link_id());
        }
        payment.setPaymentStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        //payment initiate event
        return response;
    }

    @Override
    public PaymentDTO verifyPayment(PaymentVerifyRequest req) {
        return null;
    }

    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return null;
    }
}
