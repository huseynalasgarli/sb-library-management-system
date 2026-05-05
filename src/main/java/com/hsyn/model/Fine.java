package com.hsyn.model;

import com.hsyn.domain.FineStatus;
import com.hsyn.domain.FineType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private BookLoan bookLoan;

    private FineType type;

    @Column(nullable = false)
    private Long amount;

    private FineStatus status;

    @Column(length = 500)
    private String reason;

    @Column(length = 1000)
    private String note;

    @ManyToOne
    private User waivedBy;

    @Column(name = "waived_at")
    private LocalDateTime waivedAt;

    @Column(name = "waived_reason", length = 500)
    private String waivedReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id")
    private User processedBy;

    @Column(name = "transaction_id",length = 500)
    private String transactionId;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void applyPayment(Long paymentAmount) {

        if (paymentAmount == null || paymentAmount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        // Update status based on amount paid
            this.status = FineStatus.PAID;
            this.paidAt = LocalDateTime.now();
    }

    public void waive(User admin , String reason){
        this.status = FineStatus.WAIVED;
        this.waivedBy = admin;
        this.waivedReason = reason;
        this.waivedAt = LocalDateTime.now();
    }
}
