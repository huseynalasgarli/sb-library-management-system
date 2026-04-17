package com.hsyn.service.impl;

import com.hsyn.mapper.SubscriptionPlanMapper;
import com.hsyn.model.SubscriptionPlan;
import com.hsyn.model.User;
import com.hsyn.payload.dto.SubscriptionPlanDTO;
import com.hsyn.repository.SubscriptionPlanRepository;
import com.hsyn.service.SubscriptionPlanService;
import com.hsyn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl  implements SubscriptionPlanService {

    private  final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final UserService  userService;

    @Override
    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO) throws Exception {

        if (subscriptionPlanRepository.existsByPlanCode(planDTO.getPlanCode())) {
            throw new Exception("Plan code already exist");
        }

        SubscriptionPlan plan = subscriptionPlanMapper.toEntity(planDTO);

        User currentUser = userService.getCurrentUser();
        plan.setCreatedBy(currentUser.getFullName());
        plan.setUpdatedBy(currentUser.getFullName());
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);

        return subscriptionPlanMapper.toDTO(savedPlan);
    }

    @Override
    public SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO planDTO) throws Exception {
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId).orElseThrow(
                () -> new Exception("Plan not found")
        );
        subscriptionPlanMapper.updateEntity(existingPlan, planDTO);
        User currentUser = userService.getCurrentUser();
        existingPlan.setUpdatedBy(currentUser.getFullName());
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(existingPlan);
        return subscriptionPlanMapper.toDTO(updatedPlan);
    }

    @Override
    public void deleteSubscriptionPlan(Long planId) throws Exception {
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId).orElseThrow(
                () -> new Exception("Plan not found")
        );
        subscriptionPlanRepository.delete(existingPlan);
    }

    @Override
    public List<SubscriptionPlanDTO> getAllSubscriptionPlan() {
        List<SubscriptionPlan> planList = subscriptionPlanRepository.findAll();

        return planList.stream().map(
                subscriptionPlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlan getBySubscriptionPlanCode(String subscriptionPlanCode) throws Exception {
        SubscriptionPlan plan = subscriptionPlanRepository.findByPlanCode(subscriptionPlanCode);
        if(plan == null){
            throw new Exception("Plan not found");
        }
        return plan;
    }
}
