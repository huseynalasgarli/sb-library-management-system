package com.hsyn.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SubscriptionPlan plan;

    private String planName;

    private String planCode;

    private Long price;

    @Column(nullable = false)
    private Integer maxBooksAllowed;

    @Column(nullable = false)
    private Integer maxDaysPerBook;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean isActive;

    private Boolean autoRenew;

    private LocalDateTime cancelledAt;

    private String cancellationReason;

    private String notes;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isValid(){
        if (!isActive){
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        return !now.isBefore(this.startDate) && now.isBefore(this.endDate);
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(this.endDate);
    }

    public long getDaysRemaining(){
        if (isExpired()){
            return 0;
        }

        return ChronoUnit.DAYS.between(LocalDateTime.now(),endDate);
    }

    public void calculatedEndDate(){
        if(plan!=null && startDate!=null){
            this.endDate=startDate.plusDays(plan.getDurationDays());
        }
    }
    public void initializeFromPlan(){
        if (plan == null){
            this.planName = plan.getName();
            this.planCode =plan.getPlanCode();
            this.price = plan.getPrice();
            this.maxBooksAllowed = plan.getMaxBooksAllowed();
            this.maxDaysPerBook = plan.getMaxDaysPerBook();
            if(startDate == null){
                this.startDate = LocalDateTime.now();
            }
            calculatedEndDate();
        }
    }
}
