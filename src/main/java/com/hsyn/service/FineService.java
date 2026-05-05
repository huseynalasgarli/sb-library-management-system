package com.hsyn.service;

import com.hsyn.domain.FineStatus;
import com.hsyn.domain.FineType;
import com.hsyn.payload.dto.FineDTO;
import com.hsyn.payload.request.CreateFineRequest;
import com.hsyn.payload.request.WaiveFineRequest;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.payload.response.PaymentInitiateResponse;

import java.util.List;

public interface FineService {

    FineDTO createFine(CreateFineRequest createFineRequest) throws Exception;

    PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception;

    void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception;

    FineDTO waiveFine(WaiveFineRequest req) throws Exception;

    List<FineDTO> getMyFines(FineStatus status, FineType type);

    PageResponse<FineDTO> getAllFines(FineStatus status,
                                           FineType type,
                                           Long userId,
                                           int page,
                                           int size);

}
