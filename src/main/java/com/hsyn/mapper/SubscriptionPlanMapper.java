package com.hsyn.mapper;

import com.hsyn.model.SubscriptionPlan;
import com.hsyn.payload.dto.SubscriptionPlanDTO;

public class SubscriptionPlanMapper {

    public SubscriptionPlanDTO toDTO(SubscriptionPlan plan) {
        if (plan == null) return null;

        SubscriptionPlanDTO dto = new SubscriptionPlanDTO();
        dto.setId(plan.getId());
        dto.setPlanCode(plan.getPlanCode());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setDurationDays(plan.getDurationDays());
        dto.setPrice(plan.getPrice());
        dto.setCurrency(plan.getCurrency());
        dto.setMaxBooksAllowed(plan.getMaxBooksAllowed());
        dto.setMaxDaysPerBook(plan.getMaxDaysPerBook());
        dto.setDisplayOrder(plan.getDisplayOrder());
        dto.setIsActive(plan.getIsActive());
        dto.setIsFeatured(plan.getIsFeatured());
        dto.setBadgeText(plan.getBadgeText());
        dto.setAdminNotes(plan.getAdminNotes());
        dto.setCreatedDate(plan.getCreatedDate());
        dto.setUpdatedDate(plan.getUpdatedDate());
        dto.setCreatedBy(plan.getCreatedBy());
        dto.setUpdatedBy(plan.getUpdatedBy());
        return dto;

    }

    public SubscriptionPlan toEntity(SubscriptionPlanDTO planDTO) {
        if (planDTO == null) return null;

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(planDTO.getId());
        plan.setPlanCode(planDTO.getPlanCode());
        plan.setName(planDTO.getName());
        plan.setDescription(planDTO.getDescription());
        plan.setDurationDays(planDTO.getDurationDays());
        plan.setPrice(planDTO.getPrice());
        plan.setCurrency(planDTO.getCurrency());
        plan.setMaxBooksAllowed(planDTO.getMaxBooksAllowed());
        plan.setMaxDaysPerBook(planDTO.getMaxDaysPerBook());
        plan.setDisplayOrder(planDTO.getDisplayOrder() !=null ? planDTO.getDisplayOrder() :  0);
        plan.setIsActive(planDTO.getIsActive() !=null ? planDTO.getIsActive() :  true);
        plan.setIsFeatured(planDTO.getIsFeatured() !=null ? planDTO.getIsFeatured() :  true);
        plan.setBadgeText(planDTO.getBadgeText());
        plan.setAdminNotes(planDTO.getAdminNotes());
        plan.setCreatedDate(planDTO.getCreatedDate());
        plan.setUpdatedDate(planDTO.getUpdatedDate());
        plan.setCreatedBy(planDTO.getCreatedBy());
        plan.setUpdatedBy(planDTO.getUpdatedBy());
        return plan;
    }
}
