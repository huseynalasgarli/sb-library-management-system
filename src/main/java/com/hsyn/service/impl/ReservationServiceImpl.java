package com.hsyn.service.impl;

import com.hsyn.domain.BookLoanStatus;
import com.hsyn.domain.ReservationStatus;
import com.hsyn.model.Book;
import com.hsyn.model.Reservation;
import com.hsyn.model.User;
import com.hsyn.payload.dto.ReservationDTO;
import com.hsyn.payload.request.ReservationRequest;
import com.hsyn.payload.request.ReservationSearchRequest;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.repository.BookLoanRepository;
import com.hsyn.repository.BookRepository;
import com.hsyn.repository.ReservationRepository;
import com.hsyn.service.ReservationService;
import com.hsyn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl  implements ReservationService {

    private final BookLoanRepository bookLoanRepository;
    private final UserService  userService;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

    int MAX_RESERVATION =5;

    @Override
    public ReservationDTO createReservation(ReservationRequest reservationRequest) {
        return null;
    }

    @Override
    public ReservationDTO createReservationForUser(ReservationRequest reservationRequest, Long userId) throws Exception {
        boolean alreadyHasLoan = bookLoanRepository.existsByUserIdAndBookIdAndStatus(
                userId,reservationRequest.getBookId(), BookLoanStatus.CHECKED_OUT
        );
        if (alreadyHasLoan) {
            throw new Exception("You already have loan on this book");
        }


        User user = userService.getCurrentUser();

        Book book = bookRepository.findById(reservationRequest.getBookId())
                .orElseThrow(() -> new Exception("Book not found"));

        if (reservationRepository.hasActiveReservation(userId,book.getId())){
               throw new Exception("Book has already been checked out");
        }

        if (book.getAvailableCopies()>0){
            throw new Exception("Book is already available");
        }

        long activeReservations = reservationRepository
                .countActiveReservationsByUser(userId);

        if (activeReservations>MAX_RESERVATION){
            throw new Exception("You have reserved " + MAX_RESERVATION + " times");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setNotificationSent(false);
        reservation.setNotes(reservationRequest.getNotes());


        return null;
    }

    @Override
    public ReservationDTO cancelReservation(Long reservationId) {
        return null;
    }

    @Override
    public ReservationDTO fulfillReservation(Long reservationId) {
        return null;
    }

    @Override
    public PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest) {
        return null;
    }

    @Override
    public PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest) {
        return null;
    }
}
