package com.hsyn.controller;


import com.hsyn.domain.BookLoanStatus;
import com.hsyn.exception.BookException;
import com.hsyn.exception.UserException;
import com.hsyn.payload.dto.BookLoanDTO;
import com.hsyn.payload.request.BookLoanSearchRequest;
import com.hsyn.payload.request.CheckinRequest;
import com.hsyn.payload.request.CheckoutRequest;
import com.hsyn.payload.request.RenewalRequest;
import com.hsyn.payload.response.ApiResponse;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-loans")
public class BookLoanController {

    private final BookLoanService bookLoanService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutBook(
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {

        BookLoanDTO dto = bookLoanService
                .checkoutBook(checkoutRequest);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping("/checkout/user/{userId}")
    public ResponseEntity<?> checkoutBookForUser(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {

        BookLoanDTO dto = bookLoanService
                .checkoutBookForUser(userId,checkoutRequest);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping("/checkin")
    public ResponseEntity<?> checkin(
            @Valid @RequestBody CheckinRequest checkinRequest) throws Exception {

        BookLoanDTO dto= bookLoanService
                .checkinBook(checkinRequest);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renew(
            @Valid @RequestBody RenewalRequest renewalRequest) throws Exception {
        
        BookLoanDTO dto = bookLoanService
                .renewCheckout(renewalRequest);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBookLoans(
            @RequestParam(required = false)BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws Exception {

        PageResponse<BookLoanDTO> bookLoans = bookLoanService
                .getMyBookLoans(status, page, size);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/search")
    public ResponseEntity<?> getAllBookLoans(
            @RequestBody BookLoanSearchRequest  bookLoanSearchRequest){

        PageResponse<BookLoanDTO> dto = bookLoanService
                .getBookLoans(bookLoanSearchRequest);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/admin/update-overdue")
    public ResponseEntity<?> updateOverdueBookLoans(){

        int updateCount = bookLoanService.updateOverdueBookLoan();
        return ResponseEntity.ok(
                new ApiResponse("overdue book loans are updated",true));
    }



}
