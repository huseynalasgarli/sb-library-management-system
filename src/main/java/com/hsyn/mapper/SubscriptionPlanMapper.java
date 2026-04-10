package com.hsyn.mapper;

import com.hsyn.model.SubscriptionPlan;
import com.hsyn.payload.dto.SubscriptionPlanDTO;
import org.springframework.stereotype.Component;

@Component
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
        plan.setCurrency(planDTO.getCurrency() != null ? planDTO.getCurrency() : "INR");
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

    public void updateEntity(SubscriptionPlan plan, SubscriptionPlanDTO dto) {
        if (plan == null || dto == null) return;

        if (dto.getName() != null) plan.setName(dto.getName());

        if (dto.getDescription() != null) plan.setDescription(dto.getDescription());

        if (dto.getDurationDays() != null) plan.setDurationDays(dto.getDurationDays());

        if (dto.getPrice() != null) plan.setPrice(dto.getPrice());

        if (dto.getCurrency() != null) plan.setCurrency(dto.getCurrency());

        if (dto.getMaxBooksAllowed() != null) plan.setMaxBooksAllowed(dto.getMaxBooksAllowed());

        if (dto.getMaxDaysPerBook() != null) plan.setMaxDaysPerBook(dto.getMaxDaysPerBook());

        if (dto.getDisplayOrder() != null) plan.setDisplayOrder(dto.getDisplayOrder());

        if (dto.getIsActive() != null) plan.setIsActive(dto.getIsActive());

        if (dto.getIsFeatured() != null) plan.setIsFeatured(dto.getIsFeatured());

        if (dto.getBadgeText() != null) plan.setBadgeText(dto.getBadgeText());

        if (dto.getAdminNotes() != null) plan.setAdminNotes(dto.getAdminNotes());

        if (dto.getCreatedDate() != null) plan.setCreatedDate(dto.getCreatedDate());

        if (dto.getUpdatedDate() != null) plan.setUpdatedDate(dto.getUpdatedDate());

        if (dto.getCreatedBy() != null) plan.setCreatedBy(dto.getCreatedBy());

        if (dto.getUpdatedBy() != null) plan.setUpdatedBy(dto.getUpdatedBy());
    }
}
