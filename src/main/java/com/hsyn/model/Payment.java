package com.hsyn.model;

import com.hsyn.domain.PaymentGateway;
import com.hsyn.domain.PaymentStatus;
import com.hsyn.domain.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentGateway gateway;

    private Long amount;

    private String transactionId;

    private String gatewayPaymentId;

    private String gatewayOrderId;

    private String stripeSessionId;

    private String description;
    private String failureReason;

    @CreationTimestamp
    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
