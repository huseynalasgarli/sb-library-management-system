package com.hsyn.service.impl;

import com.hsyn.domain.PaymentGateway;
import com.hsyn.domain.PaymentType;
import com.hsyn.exception.SubscriptionException;
import com.hsyn.mapper.SubscriptionMapper;
import com.hsyn.model.Subscription;
import com.hsyn.model.SubscriptionPlan;
import com.hsyn.model.User;
import com.hsyn.payload.dto.SubscriptionDTO;
import com.hsyn.payload.request.PaymentInitiateRequest;
import com.hsyn.payload.response.PaymentInitiateResponse;
import com.hsyn.repository.SubscriptionPlanRepository;
import com.hsyn.repository.SubscriptionRepository;
import com.hsyn.service.PaymentService;
import com.hsyn.service.SubscriptionService;
import com.hsyn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserService userService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentService paymentService;

    @Override
    public PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO) throws Exception {

        User user = userService.getCurrentUser();
        SubscriptionPlan plan = subscriptionPlanRepository
                .findById(subscriptionDTO.getPlanId()).orElseThrow(
                        () -> new Exception("Plan not found")
                );

        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO,plan,user);
        subscription.initializeFromPlan();
        subscription.setIsActive(false);
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        PaymentInitiateRequest paymentInitiateRequest = PaymentInitiateRequest.builder()
                .userId(user.getId())
                .subscriptionId(subscription.getId())
                .paymentType(PaymentType.MEMBERSHIP)
                .paymentGateway(PaymentGateway.STRIPE)
                .amount(subscription.getPrice())
                .description("Library Subscription - " + plan.getName())
                .build();
        return paymentService.initiatePayment(paymentInitiateRequest);
    }

    @Override
    public SubscriptionDTO getUsersActiveSubscription(Long userId) throws Exception {
        User user = userService.getCurrentUser();

        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(user.getId(), LocalDate.now())
                .orElseThrow(() -> new Exception("No active subscription found!"));
        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) throws SubscriptionException {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException(
                        "Subscription not found with ID " +subscriptionId));
        if(!subscription.getIsActive()){
            throw new SubscriptionException("Subscription is already inactive");
        }

        subscription.setIsActive(false);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason(reason!=null ? reason : "Cancelled by user");

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(
                        () -> new SubscriptionException("subscription not found by id")
                );

        subscription.setIsActive(true);
        subscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscriptions(Pageable pageable) {

        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return subscriptionMapper.toDTOList(subscriptions);
    }

    @Override
    public void deactivateExpiredSubscriptions(){
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findExpiredActiveSubscriptions(LocalDate.now());

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setIsActive(false);
            subscriptionRepository.save(subscription);
        }
    }
}
