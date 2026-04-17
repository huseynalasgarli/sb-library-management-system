package com.hsyn.service.impl;

import com.hsyn.domain.PaymentGateway;
import com.hsyn.domain.PaymentStatus;
import com.hsyn.event.publisher.PaymentEventPublisher;
import com.hsyn.exception.PaymentException;
import com.hsyn.mapper.PaymentMapper;
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
import com.stripe.model.checkout.Session;
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
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;

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
    public PaymentDTO verifyPayment(PaymentVerifyRequest req) throws PaymentException {

        // use the existing method to fetch session details
        Session session = stripeService.fetchPaymentDetails(req.getStripeSessionId());

        // get payment from DB using payment_id we stored in metadata
        Long paymentId = Long.valueOf(session.getMetadata().get("payment_id"));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        boolean isValid = stripeService.isValidPayment(req);

        if (payment.getGateway() == PaymentGateway.STRIPE) {
            if (isValid) {
                payment.setGatewayOrderId(req.getStripeSessionId());
            }
        }

        if (isValid) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            payment = paymentRepository.save(payment);

            // publish payment success event todo
            paymentEventPublisher.publishPaymentSuccessEvent(payment);
        }

        return paymentMapper.toDTO(payment);
    }
    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return payments.map(paymentMapper::toDTO);
    }
}
