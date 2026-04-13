package com.hsyn.service;

import com.hsyn.model.SubscriptionPlan;
import com.hsyn.payload.dto.SubscriptionPlanDTO;

import java.util.List;


public interface SubscriptionPlanService {

    SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO) throws Exception;
    SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO planDTO) throws Exception;
    void deleteSubscriptionPlan(Long planId) throws Exception;

    List<SubscriptionPlanDTO> getAllSubscriptionPlan();

    SubscriptionPlan getBySubscriptionPlanCode(String subscriptionPlanCode) throws Exception;

}
