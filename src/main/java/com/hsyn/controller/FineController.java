package com.hsyn.controller;

import com.hsyn.domain.FineStatus;
import com.hsyn.domain.FineType;
import com.hsyn.payload.dto.FineDTO;
import com.hsyn.payload.request.CreateFineRequest;
import com.hsyn.payload.request.WaiveFineRequest;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.payload.response.PaymentInitiateResponse;
import com.hsyn.service.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fines")
public class FineController {

    private final FineService finesService;

    @PostMapping
    public ResponseEntity<?> createFine(
            @Valid @RequestBody CreateFineRequest req
            ) throws Exception {
        FineDTO dto = finesService.createFine(req);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payFine(
            @PathVariable Long id,
            @RequestParam(required = false) String transactionId
    ) throws Exception {
        PaymentInitiateResponse res= finesService.payFine(id, transactionId);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/waive")
    public ResponseEntity<?> waiveFine(
            @Valid @RequestBody WaiveFineRequest req
            ) throws Exception {
        FineDTO dto = finesService.waiveFine(req);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyFines(
            @RequestParam(required = false)FineStatus status,
            @RequestParam(required = false) FineType type
            ) throws Exception {
        List<FineDTO> fines = finesService.getMyFines(status, type);
        return ResponseEntity.ok(fines);
    }

    @GetMapping
    public ResponseEntity<?> getAllFines(
            @RequestParam(required = false) FineStatus status,
            @RequestParam(required = false) FineType type,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        PageResponse<FineDTO> fines = finesService
                .getAllFines(status, type, userId, page, size);

        return ResponseEntity.ok(fines);
    }
}
