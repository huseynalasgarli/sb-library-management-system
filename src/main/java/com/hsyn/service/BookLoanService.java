package com.hsyn.service;

import com.hsyn.domain.BookLoanStatus;
import com.hsyn.payload.dto.BookLoanDTO;
import com.hsyn.payload.request.BookLoanSearchRequest;
import com.hsyn.payload.request.CheckinRequest;
import com.hsyn.payload.request.CheckoutRequest;
import com.hsyn.payload.request.RenewalRequest;
import com.hsyn.payload.response.PageResponse;
import org.springframework.data.domain.PageRequest;

public interface BookLoanService {

    BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest) throws Exception;

    BookLoanDTO checkoutBookForUser(Long userId,CheckoutRequest checkoutRequest) throws Exception;

    BookLoanDTO checkinBook(CheckinRequest checkinRequest) throws Exception;

    BookLoanDTO renewCheckout(RenewalRequest renewalRequest) throws Exception;

    PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status,
                                             int page, int size) throws Exception;

    PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request);


    int updateOverdueBookLoan();
}
