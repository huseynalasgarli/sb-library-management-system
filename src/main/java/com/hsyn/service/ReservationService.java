package com.hsyn.service;

import com.hsyn.payload.dto.ReservationDTO;
import com.hsyn.payload.request.ReservationRequest;
import com.hsyn.payload.request.ReservationSearchRequest;
import com.hsyn.payload.response.PageResponse;

public interface ReservationService {

    ReservationDTO createReservation(ReservationRequest reservationRequest);

    ReservationDTO createReservationForUser(ReservationRequest reservationRequest,
                                            Long userId) throws Exception;

    ReservationDTO cancelReservation(Long reservationId);
    ReservationDTO fulfillReservation(Long reservationId);

    PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest);
    PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest);
}
