package com.hsyn.service;


import com.hsyn.exception.PaymentException;
import com.hsyn.payload.dto.PaymentDTO;
import com.hsyn.payload.request.PaymentInitiateRequest;
import com.hsyn.payload.request.PaymentVerifyRequest;
import com.hsyn.payload.response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest req) throws PaymentException;

    PaymentDTO verifyPayment(PaymentVerifyRequest req);

    Page<PaymentDTO> getAllPayments(Pageable pageable);
}
