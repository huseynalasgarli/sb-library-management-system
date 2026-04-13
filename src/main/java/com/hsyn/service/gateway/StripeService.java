package com.hsyn.service.gateway;

import com.hsyn.domain.PaymentType;
import com.hsyn.exception.PaymentException;
import com.hsyn.model.Payment;
import com.hsyn.model.SubscriptionPlan;
import com.hsyn.model.User;
import com.hsyn.payload.response.PaymentLinkResponse;
import com.hsyn.service.SubscriptionPlanService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.model.PaymentIntent;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    private final SubscriptionPlanService subscriptionPlanService;

    @Value("${stripe.api.key.secret}")
    private String stripeSecretKey;

    @Value("${stripe.callback-base-url:http://localhost:5173}")
    private String callbackBaseUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public PaymentLinkResponse createPaymentLink(User user, Payment payment) {
        try {
            String successUrl = callbackBaseUrl + "/payment-success/" + payment.getId() + "?session_id={CHECKOUT_SESSION_ID}";
            String cancelUrl = callbackBaseUrl + "/payment-cancel/" + payment.getId();

            // Build metadata (equivalent to Razorpay notes)
            Map<String, String> metadata = new HashMap<>();
            metadata.put("user_id", String.valueOf(user.getId()));
            metadata.put("payment_id", String.valueOf(payment.getId()));

            if (payment.getPaymentType() == PaymentType.MEMBERSHIP) {
                metadata.put("subscription_id", String.valueOf(payment.getSubscription().getId()));
                metadata.put("plan", payment.getSubscription().getPlan().getPlanCode());
                metadata.put("type", PaymentType.MEMBERSHIP.toString());
            } else if (payment.getPaymentType() == PaymentType.FINE) {
                metadata.put("type", PaymentType.FINE.toString());
            }

            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomerEmail(user.getEmail())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .putAllMetadata(metadata)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(payment.getAmount() * 100L) // Stripe uses cents
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(payment.getDescription())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    );

            Session session = Session.create(paramsBuilder.build());

            PaymentLinkResponse response = new PaymentLinkResponse();
            response.setPayment_link_id(session.getId());
            response.setPayment_link_url(session.getUrl());
            return response;

        } catch (StripeException e) {
            log.error("Failed to create Stripe payment link: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment link: " + e.getMessage(), e);
        }
    }

    public PaymentIntent fetchPaymentDetails(String paymentIntentId) throws PaymentException {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            log.error("Failed to fetch payment details for {}: {}", paymentIntentId, e.getMessage(), e);
            throw new PaymentException("Failed to fetch payment details: " + e.getMessage());
        }
    }

    public boolean isValidPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = fetchPaymentDetails(paymentIntentId);

            // 1. Check status
            if (!"succeeded".equalsIgnoreCase(paymentIntent.getStatus())) {
                return false;
            }

            // 2. Extract metadata
            Map<String, String> metadata = paymentIntent.getMetadata();
            String paymentType = metadata.get("type");
            long amountInDollars = paymentIntent.getAmountReceived() / 100L;

            // 3. Check expected amount
            if (PaymentType.MEMBERSHIP.toString().equals(paymentType)) {
                String planCode = metadata.get("plan");
                SubscriptionPlan subscriptionPlan = subscriptionPlanService
                        .getBySubscriptionPlanCode(planCode);
                return amountInDollars == subscriptionPlan.getPrice();

            } else if (PaymentType.FINE.toString().equals(paymentType)) {
//                Long fineId = Long.valueOf(metadata.get("fine_id"));
//                Fine fine = fineRepository.findById(fineId)
//                        .orElseThrow(() -> new FineException("Fine not found"));
//                return fine.getAmount() == amountInDollars;
                return true;
            }

            return false;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}