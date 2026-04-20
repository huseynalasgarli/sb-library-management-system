package com.hsyn.service.impl;

import com.hsyn.domain.BookLoanStatus;
import com.hsyn.domain.BookLoanType;
import com.hsyn.exception.BookException;
import com.hsyn.mapper.BookLoanMapper;
import com.hsyn.model.Book;
import com.hsyn.model.BookLoan;
import com.hsyn.model.User;
import com.hsyn.payload.dto.BookLoanDTO;
import com.hsyn.payload.dto.SubscriptionDTO;
import com.hsyn.payload.request.BookLoanSearchRequest;
import com.hsyn.payload.request.CheckinRequest;
import com.hsyn.payload.request.CheckoutRequest;
import com.hsyn.payload.request.RenewalRequest;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.repository.BookLoanRepository;
import com.hsyn.repository.BookRepository;
import com.hsyn.service.BookLoanService;
import com.hsyn.service.SubscriptionService;
import com.hsyn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {

    private  final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final BookRepository bookRepository;
    private final BookLoanMapper bookLoanMapper;


    @Override
    public BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest) throws Exception {

        User user = userService.getCurrentUser();

        return checkoutBookForUser(user.getId(),checkoutRequest);
    }

    @Override
    public BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws Exception {
        // 1. validate user exists
        User user = userService.findById(userId);

        // 2. validate user has active subscription
        SubscriptionDTO subscription = subscriptionService
                .getUsersActiveSubscription(user.getId());

        // 3. validate book exists and is available
        Book book = bookRepository.findById(checkoutRequest.getBookId())
                .orElseThrow(
                        () -> new BookException("Book not found with id " + checkoutRequest.getBookId()));
        if(!book.getActive()){
            throw new BookException("Book is not active.");
        }
        if (book.getAvailableCopies()<=0){
            throw new BookException("Book has no available copies.");
        }


        // 4. check if user already has this book checkout
        if (bookLoanRepository.hasActiveCheckout(userId,book.getId())){
            throw new BookException("Book has already checked out.");
        }

        // 5.check user's active checkout limit
        long activeCheckouts= bookLoanRepository.countActiveBookLoansByUser(userId);
        int maxBooksAllowed = subscription.getMaxBooksAllowed();

        if (activeCheckouts>=maxBooksAllowed){
            throw new BookException("You have reached your maximum number of books allowed.");
        }

        // 6. check for overdue books
        long overdueCount = bookLoanRepository.countOverdueBookLoansByUser(userId);
        if(overdueCount>0){
            throw new BookException("First return old overdue books!");
        }

        // 7. create book loan
        BookLoan bookLoan = BookLoan.builder()
                .user(user)
                .book(book)
                .type(BookLoanType.CHECKOUT)
                .status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(checkoutRequest.getCheckoutDays()))
                .renewalCount(0)
                .maxRenewals(2)
                .notes(checkoutRequest.getNotes())
                .isOverdue(false)
                .overdueDays(0)
                .build();

        // 9. update book available copies
        book.setAvailableCopies(book.getAvailableCopies()-1);
        bookRepository.save(book);

        // 10. save book loan
        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO checkinBook(CheckinRequest checkinRequest) throws Exception {

        // 1. validate book loan exists
        BookLoan bookLoan = bookLoanRepository.findById(checkinRequest.getBookLoanId())
                .orElseThrow(
                        () -> new Exception("Book loan not found!"));

        // 2. check if already returned
        if (!bookLoan.isActive()){
            throw new BookException("Book loan is not active.");
        }

        // 3. set return date
        bookLoan.setReturnDate(LocalDate.now());

        // 4.
        BookLoanStatus condition = checkinRequest.getCondition();
        if(condition == null){
            condition= BookLoanStatus.RETURNED;
        }
        bookLoan.setStatus(condition);

        // 5 fine todo
        bookLoan.setOverdueDays(0);
        bookLoan.setIsOverdue(false);
        // 6.
        bookLoan.setNotes("Book returned by user");
        // 7. update book availability

        if (condition!=BookLoanStatus.LOST){
            Book book = bookLoan.getBook();
            book.setAvailableCopies(book.getAvailableCopies()-1);
            bookRepository.save(book);
        }
        // process next reservation todo

        // 8.
        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);
        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO renewCheckout(RenewalRequest renewalRequest) throws Exception {
        // 1. validate book loan exists
        BookLoan bookLoan = bookLoanRepository.findById(renewalRequest.getBookLoanId())
                .orElseThrow(
                        () -> new Exception("Book loan not found!"));

        // 2. check if book can be renewed
        if (!bookLoan.canRenew()){
            throw new BookException("Book cannot be renewed.");
        }

        // 3. update due date
        bookLoan.setDueDate(bookLoan.getDueDate()
                .plusDays(renewalRequest.getExtensionDays()));

        bookLoan.setRenewalCount(bookLoan.getRenewalCount()+1);

        bookLoan.setNotes("book renewed by user");

        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) {
        return null;
    }

    @Override
    public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request) {

        User currentUser = userService.getCurrentUser();
        Page<BookLoan> bookLoanPage;
        if (status!=null) {
            Pageable pageable = PageRequest.of(page,size, Sort.by("dueDate").ascending());
            bookLoanPage=bookLoanRepository.findByStatusAndUser(
                    status,currentUser,pageable);
        }
        else {

        }
        return null;
    }

    @Override
    public int updateOverdueBookLoan() {
        return 0;
    }
}
