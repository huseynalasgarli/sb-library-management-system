package com.hsyn.payload.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {

    private Long id;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private String username;
    private String userEmail;

    @NotNull(message = "Subscription plan ID is mandatory")
    private Long planId;

    private String planName;
    private String planCode;
    private Long price;
    private String currency;
    private Double priceInMajorUnits;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Integer maxBooksAllowed;
    private Integer maxDaysPerBook;
    private Boolean autoRenew;
    private LocalDateTime cancellationDate;
    private String cancellationReason;
    private String notes;
    private Long daysRemaining;
    private Boolean isValid;
    private Boolean isExpired;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
