package com.hsyn.service;

import com.hsyn.domain.FineStatus;
import com.hsyn.domain.FineType;
import com.hsyn.domain.PaymentGateway;
import com.hsyn.domain.PaymentType;
import com.hsyn.mapper.FineMapper;
import com.hsyn.model.BookLoan;
import com.hsyn.model.Fine;
import com.hsyn.model.User;
import com.hsyn.payload.dto.FineDTO;
import com.hsyn.payload.request.CreateFineRequest;
import com.hsyn.payload.request.PaymentInitiateRequest;
import com.hsyn.payload.request.WaiveFineRequest;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.payload.response.PaymentInitiateResponse;
import com.hsyn.repository.BookLoanRepository;
import com.hsyn.repository.FineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService{

    private final BookLoanRepository bookLoanRepository;
    private final FineRepository fineRepository;
    private final FineMapper fineMapper;
    private final UserService userService;
    private final PaymentService paymentService;
    private final WaiveFineRequest  waiveFineRequest;

    @Override
    public FineDTO createFine(CreateFineRequest createFineRequest) throws Exception {

        // 1. validate book loan exists
        BookLoan bookLoan = bookLoanRepository.findById(createFineRequest.getBookLoanId())
                .orElseThrow(() -> new Exception("Book loan does not exists"));

        // 2. create a fine

        Fine fine = Fine.builder()
                .bookLoan(bookLoan)
                .user(bookLoan.getUser())
                .type(createFineRequest.getType())
                .amount(createFineRequest.getAmount())
                .status(FineStatus.PENDING)
                .reason(createFineRequest.getReason())
                .note(createFineRequest.getNotes())
                .build();

        Fine savedFine = fineRepository.save(fine);
        return fineMapper.toDTO(savedFine);
    }

    @Override
    public PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception {

        // 1. validate fine exist

        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new Exception("Fine does not exists"));

        // 2. check if already paid
        if (fine.getStatus() == FineStatus.PAID) {
            throw new Exception("Fine is already paid");
        }
        if (fine.getStatus() == FineStatus.WAIVED) {
            throw new Exception("Fine is already waived");
        }

        // initiate payment

        User user = userService.getCurrentUser();

        PaymentInitiateRequest request = PaymentInitiateRequest.builder()
                .userId(user.getId())
                .fineId(fine.getId())
                .paymentType(PaymentType.FINE)
                .paymentGateway(PaymentGateway.STRIPE)
                .amount(fine.getAmount())
                .description("Library fine payment")
                .build();
        return paymentService.initiatePayment(request);
    }

    @Override
    public void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception {
        Fine fine= fineRepository.findById(fineId)
                .orElseThrow(() -> new Exception(
                        "Fine not found with id:" + fineId
                ));

        fine.applyPayment(amount);
        fine.setTransactionId(transactionId);
        fine.setStatus(FineStatus.PAID);
        fine.setUpdatedAt(LocalDateTime.now());

        fineRepository.save(fine);
    }

    @Override
    public FineDTO waiveFine(WaiveFineRequest req) throws Exception {
        Fine fine = fineRepository.findById(waiveFineRequest.getFineId())
                .orElseThrow(() -> new Exception("Fine not found"));

        if (fine.getStatus() == FineStatus.WAIVED) {
            throw  new Exception("Fine is already waived");
        }

        if (fine.getStatus() == FineStatus.PAID) {
            throw new Exception("Fine is already paid");
        }

        User currentAdmin = userService.getCurrentUser();
        fine.waive(currentAdmin,waiveFineRequest.getReason());

        Fine savedFine = fineRepository.save(fine);

        return fineMapper.toDTO(savedFine);
    }

    @Override
    public List<FineDTO> getMyFines(FineStatus status, FineType type) {
        User currentUser = userService.getCurrentUser();
        List<Fine> fine;

        if (status != null && type != null) {

            fine = fineRepository.findByUserId(currentUser.getId()).stream()
                    .filter(f-> f.getStatus() == status && f.getType() == type)
                    .toList();
        }
        return List.of();
    }


    @Override
    public PageResponse<FineDTO> getAllFines(FineStatus status, FineType type, Long userId, int page, int size) {
        return null;
    }
}
