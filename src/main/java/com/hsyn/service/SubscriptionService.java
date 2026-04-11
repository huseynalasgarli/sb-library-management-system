package com.hsyn.service;



import com.hsyn.payload.dto.SubscriptionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDTO subscribe(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO getUsersActiveSubscription(Long userId);

    SubscriptionDTO cancelSubscription(Long subscriptionId,String reason);

    SubscriptionDTO activeSubscription(Long subscriptionId,Long paymentId);

    List<SubscriptionDTO> getSubscriptions(Pageable pageable);

}
