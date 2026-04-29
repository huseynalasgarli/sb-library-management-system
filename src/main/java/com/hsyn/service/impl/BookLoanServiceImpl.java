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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

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
    public PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) throws Exception {
        User currentUser = userService.getCurrentUser();

        Pageable pageable = status != null
                ? PageRequest.of(page, size, Sort.by("dueDate").ascending())
                : PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<BookLoan> bookLoanPage = status != null
                ? bookLoanRepository.findByStatusAndUser(status, currentUser, pageable)
                : bookLoanRepository.findByUserId(currentUser.getId(), pageable);

        if (bookLoanPage.isEmpty()) {
            throw new Exception("No book loans found");
        }

        return convertToPageResponse(bookLoanPage);
    }

    @Override
    public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request) {
        // 1. build pageable with sorting, size, etc.
        Pageable pageable = createPageable(
                request.getPage(),
                request.getSize(),
                request.getSortBy(),
                request.getSortDirection()
        );

        Page<BookLoan> bookLoanPage;

        // 2. apply filtering logic dynamically
        if (Boolean.TRUE.equals(request.getOverdueOnly())){
            // fetch overdue loans
            bookLoanPage = bookLoanRepository.findOverdueBookLoans(LocalDate.now(),pageable);
        }
        else if (request.getUserId()!=null){
            // fetch loans by specific user
            bookLoanPage = bookLoanRepository.findByUserId(request.getUserId(), pageable);
        } else if (request.getBookId()!=null) {
            // fetch loans by specific book
            bookLoanPage = bookLoanRepository.findByBookId(request.getBookId(),pageable);
        }
        else if (request.getStatus()!=null){
            // fetch loans by loan status
            bookLoanPage = bookLoanRepository.findByStatus(request.getStatus(), pageable);
        } else if (request.getStartDate()!=null && request.getEndDate()!=null) {
            // fetch loans within date range
            bookLoanPage = bookLoanRepository.findBookLoansByDateRange(
                    request.getStartDate(),
                    request.getEndDate(),
                    pageable
            );
        }
        else {
            // default: return all loans
            bookLoanPage = bookLoanRepository.findAll(pageable);
        }

        // 3. convert entities to DTOs and wrap it in response object
        return convertToPageResponse(bookLoanPage);
    }

    @Override
    public int updateOverdueBookLoan() {

        Pageable pageable = PageRequest.of(0,1000); // Process in batch
        Page<BookLoan> overduePage = bookLoanRepository
                .findOverdueBookLoans(LocalDate.now(),pageable);

        int updateCount = 0;
        for (BookLoan bookLoan : overduePage.getContent()) {
            if (bookLoan.getStatus() == BookLoanStatus.CHECKED_OUT) {
                bookLoan.setStatus(BookLoanStatus.OVERDUE);
                bookLoan.setIsOverdue(true);
            }

//            // Calculate overdue days
            int overdueDays = calculateOverdueDate(bookLoan.getDueDate(),LocalDate.now());

            // Calculate fine
//            BigDecimal fine = fineCalculationService.calculateOverdueFine(bookLoan);

            bookLoanRepository.save(bookLoan);
            updateCount++;
        }

        return updateCount;
    }

    private Pageable createPageable(int page, int size , String sortBy,String sortDirection){
        size = Math.min(size,100);
        size = Math.max(size,1);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page,size,sort);
    }

    private PageResponse<BookLoanDTO> convertToPageResponse(Page<BookLoan> bookLoanPage){
        List<BookLoanDTO> bookLoanDTOS = bookLoanPage.getContent()
                .stream()
                .map(bookLoanMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                bookLoanDTOS,
                bookLoanPage.getNumber(),
                bookLoanPage.getSize(),
                bookLoanPage.getTotalElements(),
                bookLoanPage.getTotalPages(),
                bookLoanPage.isLast(),
                bookLoanPage.isFirst(),
                bookLoanPage.isEmpty()
        );
    }

    public int calculateOverdueDate(LocalDate dueDate, LocalDate today){
        if (dueDate.isAfter(today) || dueDate.isEqual(today)) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(dueDate, today);
    }



}
