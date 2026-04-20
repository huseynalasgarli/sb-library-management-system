package com.hsyn.repository;

import com.hsyn.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("select s from Subscription s where s.user.id = :userId AND " +
            "s.isActive = true and " +
            "s.startDate <= :today and s.endDate >= :today")
    Optional<Subscription> findActiveSubscriptionByUserId(
            @Param("userId") Long userId,
            @Param("today") LocalDateTime today
    );

    @Query("select s from Subscription s where s.isActive = true " +
            "AND s.endDate <= :today")
    List<Subscription> findExpiredActiveSubscriptions(
            @Param("today") LocalDateTime today
    );
}

