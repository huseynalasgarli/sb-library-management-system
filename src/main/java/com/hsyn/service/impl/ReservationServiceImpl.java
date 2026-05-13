package com.hsyn.service.impl;

import com.hsyn.domain.BookLoanStatus;
import com.hsyn.domain.ReservationStatus;
import com.hsyn.domain.UserRole;
import com.hsyn.mapper.ReservationMapper;
import com.hsyn.model.Book;
import com.hsyn.model.Reservation;
import com.hsyn.model.User;
import com.hsyn.payload.dto.ReservationDTO;
import com.hsyn.payload.request.CheckoutRequest;
import com.hsyn.payload.request.ReservationRequest;
import com.hsyn.payload.request.ReservationSearchRequest;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.repository.BookLoanRepository;
import com.hsyn.repository.BookRepository;
import com.hsyn.repository.ReservationRepository;
import com.hsyn.service.BookLoanService;
import com.hsyn.service.ReservationService;
import com.hsyn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl  implements ReservationService {

    private final BookLoanRepository bookLoanRepository;
    private final UserService  userService;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final BookLoanService bookLoanService;

    int MAX_RESERVATION =5;
    private static final List<ReservationStatus> ACTIVE_STATUSES = List.of(
            ReservationStatus.PENDING,
            ReservationStatus.AVAILABLE
    );

    @Override
    public ReservationDTO createReservation(ReservationRequest reservationRequest) throws Exception {
        User user = userService.getCurrentUser();
        return createReservationForUser(reservationRequest,user.getId());
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

        if (reservationRepository.hasActiveReservation(userId,book.getId(),ACTIVE_STATUSES)) {
               throw new Exception("Book has already been checked out");
        }

        if (book.getAvailableCopies()>0){
            throw new Exception("Book is already available");
        }

        long activeReservations = reservationRepository
                .countActiveReservationsByUser(userId,ACTIVE_STATUSES);

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

        long pendingCount = reservationRepository.countPendingReservationsByBook(
                book.getId()
        );
        reservation.setQueuePosition((int)pendingCount+1);
        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public ReservationDTO cancelReservation(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new Exception("Reservation not found with ID " + reservationId));

        User currentUser = userService.getCurrentUser();
        if (!reservation.getUser().getId().equals(currentUser.getId())
            && currentUser.getRole()!= UserRole.ROLE_ADMIN){
            throw new Exception("You can only cancel your own reservations");
        }
        if (!reservation.canBeCancelled()){
            throw new Exception("Reservation cannot be cancelled (current status: " +reservation.getStatus() + ")");
        }


        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);

//        updateQueuePositions(reservation.getBook().getId());


        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public ReservationDTO fulfillReservation(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new Exception("Reservation not found with ID " + reservationId));
        if (reservation.getBook().getAvailableCopies()<=0){
            throw new Exception("Reservation has no available copies");
        }
        reservation.setStatus(ReservationStatus.FULFILLED);
        reservation.setFulfilledAt(LocalDateTime.now());

        CheckoutRequest request = new CheckoutRequest();
        request.setBookId(reservation.getBook().getId());
        request.setNotes("Assign booked by Admin");

        bookLoanService.checkoutBookForUser(reservation.getUser().getId(),request);

        return reservationMapper.toDTO(reservation);
    }

    @Override
    public PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest) {
        User user = userService.getCurrentUser();
        searchRequest.setUserId(user.getId());
        return searchReservations(searchRequest);
    }

    @Override
    public PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest) {
        Pageable pageable = createPageable(searchRequest);

        Page<Reservation> reservationPage =  reservationRepository.searchReservationsWithFilters(
                searchRequest.getUserId(),
                searchRequest.getBookId(),
                searchRequest.getStatus(),
                searchRequest.getActiveOnly() !=null ? searchRequest.getActiveOnly() : false,
                ACTIVE_STATUSES,
                pageable
        );
        return buildPageResponse(reservationPage);
    }

    private Pageable createPageable(ReservationSearchRequest searchRequest) {
        Sort sort = "ASC".equalsIgnoreCase(searchRequest.getSortDirection())
                ? Sort.by(searchRequest.getSortBy()).ascending()
                : Sort.by(searchRequest.getSortBy()).descending();
        return PageRequest.of(searchRequest.getPage(),searchRequest.getSize(),sort);
    }

    private PageResponse<ReservationDTO> buildPageResponse(Page<Reservation> reservationPage) {
        List<ReservationDTO> dto = reservationPage.getContent().stream()
                .map(reservationMapper::toDTO)
                .toList();

        PageResponse<ReservationDTO> response = new PageResponse<>();
        response.setContent(dto);
        response.setPageNumber(reservationPage.getNumber());
        response.setPageSize(reservationPage.getSize());
        response.setTotalElements(reservationPage.getTotalElements());
        response.setTotalPages(reservationPage.getTotalPages());
        response.setLast(reservationPage.isLast());

        return response;
    }
}
