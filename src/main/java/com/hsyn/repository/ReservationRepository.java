package com.hsyn.repository;

import com.hsyn.domain.ReservationStatus;
import com.hsyn.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.book.id = :bookId " +
            "AND r.status = com.hsyn.domain.ReservationStatus.PENDING ORDER BY r.reservedAt ASC")
    List<Reservation> findPendingReservationsByBook(@Param("bookId") Long bookId);

    @Query("SELECT r FROM Reservation r WHERE r.book.id = :bookId " +
            "AND r.status = com.hsyn.domain.ReservationStatus.PENDING ORDER BY r.reservedAt ASC LIMIT 1")
    Optional<Reservation> findNextPendingReservation(@Param("bookId") Long bookId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
            "WHERE r.user.id = :userId AND r.book.id = :bookId " +
            "AND r.status IN :activeStatuses")
    boolean hasActiveReservation(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId " +
            "AND r.status IN :activeStatuses")
    long countActiveReservationsByUser(
            @Param("userId") Long userId,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.id = :bookId " +
            "AND r.status = com.hsyn.domain.ReservationStatus.PENDING")
    Long countPendingReservationsByBook(@Param("bookId") Long bookId);

    @Query("SELECT r FROM Reservation r WHERE r.status = com.hsyn.domain.ReservationStatus.AVAILABLE " +
            "AND r.availableUntil < :currentDateTime")
    List<Reservation> findExpiredReservations(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.book.id = :bookId " +
            "AND r.status IN :activeStatuses")
    Optional<Reservation> findActiveReservationByUserAndBook(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );

    @Query("SELECT r FROM Reservation r WHERE " +
            "(:userId IS NULL OR r.user.id = :userId) AND " +
            "(:bookId IS NULL OR r.book.id = :bookId) AND " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:activeOnly = false OR r.status IN :activeStatuses)")
    Page<Reservation> searchReservationsWithFilters(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("status") ReservationStatus status,
            @Param("activeOnly") boolean activeOnly,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses,
            Pageable pageable
    );
}