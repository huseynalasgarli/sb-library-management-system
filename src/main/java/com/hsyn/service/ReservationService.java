package com.hsyn.service;

import com.hsyn.payload.dto.ReservationDTO;
import com.hsyn.payload.request.ReservationRequest;
import com.hsyn.payload.request.ReservationSearchRequest;
import com.hsyn.payload.response.PageResponse;

public interface ReservationService {

    ReservationDTO createReservation(ReservationRequest reservationRequest) throws Exception;

    ReservationDTO createReservationForUser(ReservationRequest reservationRequest,
                                            Long userId) throws Exception;

    ReservationDTO cancelReservation(Long reservationId) throws Exception;
    ReservationDTO fulfillReservation(Long reservationId) throws Exception;

    PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest);
    PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest);
}
