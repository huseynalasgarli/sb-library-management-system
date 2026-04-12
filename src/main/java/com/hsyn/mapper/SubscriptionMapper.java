package com.hsyn.mapper;

import com.hsyn.exception.SubscriptionException;
import com.hsyn.model.Subscription;
import com.hsyn.model.SubscriptionPlan;
import com.hsyn.model.User;
import com.hsyn.payload.dto.SubscriptionDTO;
import com.hsyn.repository.SubscriptionPlanRepository;
import com.hsyn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper {

    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;

    public SubscriptionDTO toDTO(Subscription subscription) {
        if(subscription == null) return null;

        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());

        if (subscription.getUser() != null) {
            dto.setUserId(subscription.getUser().getId());
            dto.setUsername(subscription.getUser().getFullName());
            dto.setUserEmail(subscription.getUser().getEmail());
        }

        if (subscription.getPlan() != null) {
            dto.setPlanId(subscription.getPlan().getId());
        }

        dto.setPlanName(subscription.getPlanName());
        dto.setPlanCode(subscription.getPlanCode());
        dto.setPrice(subscription.getPrice());

        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setIsActive(subscription.getIsActive());
        dto.setMaxBooksAllowed(subscription.getMaxBooksAllowed());
        dto.setMaxDaysPerBook(subscription.getMaxDaysPerBook());
        dto.setAutoRenew(subscription.getAutoRenew());
        dto.setCancellationDate(subscription.getCancelledAt());
        dto.setCancellationReason(subscription.getCancellationReason());
        dto.setNotes(subscription.getNotes());
        dto.setCreatedDate(subscription.getCreatedAt());
        dto.setUpdatedDate(subscription.getUpdatedAt());

        dto.setDaysRemaining(subscription.getDaysRemaining());
        dto.setIsValid(subscription.isValid());
        dto.setIsExpired(subscription.isExpired());

        return dto;
    }

    public Subscription toEntity(SubscriptionDTO dto,
                                 SubscriptionPlan plan,
                                 User user) throws SubscriptionException {
        if(dto == null) return null;

        Subscription subscription = new Subscription();
        subscription.setId(dto.getId());
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setNotes(dto.getNotes());

        return  subscription;
    }

    public List<SubscriptionDTO> toDTOList(List<Subscription> subscriptions) {
        if(subscriptions == null) return null;

        return subscriptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


}
