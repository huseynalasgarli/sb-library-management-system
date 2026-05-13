package com.hsyn.controller;

import com.hsyn.domain.ReservationStatus;
import com.hsyn.payload.dto.ReservationDTO;
import com.hsyn.payload.request.ReservationRequest;
import com.hsyn.payload.request.ReservationSearchRequest;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(
            @Valid @RequestBody ReservationRequest reservationRequest) throws Exception {
        ReservationDTO reservationDTO = reservationService.createReservation(reservationRequest);
        return ResponseEntity.ok().body(reservationDTO);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createReservationForUser(
            @PathVariable Long userId,
            @Valid @RequestBody ReservationRequest reservationRequest) throws Exception {
        ReservationDTO reservationDTO = reservationService
                .createReservationForUser(reservationRequest,userId);
        return new ResponseEntity<>(reservationDTO,HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long id) throws Exception {
        ReservationDTO reservationDTO = reservationService.cancelReservation(id);
        return ResponseEntity.ok().body(reservationDTO);
    }

    @PostMapping("/{id}/fulfill")
    public ResponseEntity<?> fulfillReservation(
            @PathVariable Long id) throws Exception {
        ReservationDTO reservationDTO = reservationService.fulfillReservation(id);
        return ResponseEntity.ok().body(reservationDTO);
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<ReservationDTO>> getMyReservations(
            @RequestParam(required = false)ReservationStatus status,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        ReservationSearchRequest searchRequest = new ReservationSearchRequest();
        searchRequest.setStatus(status);
        searchRequest.setActiveOnly(activeOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<ReservationDTO> pageResponse = reservationService.getMyReservations(searchRequest);
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReservationDTO>> searchReservations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false)ReservationStatus status,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        ReservationSearchRequest searchRequest = new ReservationSearchRequest();
        searchRequest.setUserId(userId);
        searchRequest.setBookId(bookId);
        searchRequest.setStatus(status);
        searchRequest.setActiveOnly(activeOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<ReservationDTO> pageResponse = reservationService.searchReservations(searchRequest);
        return ResponseEntity.ok(pageResponse);
    }
}
