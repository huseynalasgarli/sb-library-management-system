package com.hsyn.repository;

import com.hsyn.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan,Long> {

    Boolean existsByPlanCode(String planCode);
}

