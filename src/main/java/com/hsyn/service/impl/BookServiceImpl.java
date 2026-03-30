package com.hsyn.service.impl;

import com.hsyn.exception.BookException;
import com.hsyn.mapper.BookMapper;
import com.hsyn.model.Book;
import com.hsyn.payload.dto.BookDTO;
import com.hsyn.payload.request.BookSearchRequest;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.repository.BookRepository;
import com.hsyn.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDTO createBook(BookDTO bookDTO) throws BookException {

        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new BookException("Book with ISBN" + bookDTO.getIsbn() + " already exists.");
        }
        Book book = bookMapper.toEntity(bookDTO);

        //total = 10
        book.isAvailableCopiesValid();

        Book saved = bookRepository.save(book);
        return bookMapper.toDTO(saved);
    }

    @Override
    public List<BookDTO> createBooksBulk(List<BookDTO> bookDTOS) throws BookException {

        List<BookDTO> createdBooks = new ArrayList<>();
        for(BookDTO bookDTO: bookDTOS){
            BookDTO book = createBook(bookDTO);
            createdBooks.add(book);
        }
        return createdBooks;
    }

    @Override
    public BookDTO getBookById(long bookId) throws BookException {
        Book book =  bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException("Book with ID" + bookId + " not found."));
        return bookMapper.toDTO(book);
    }

    @Override
    public BookDTO getBookByISBN(String isbn) throws BookException {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookException("Book with ISBN " + isbn + " not found."));
    return bookMapper.toDTO(book);
    }

    @Override
    public BookDTO updateBook(long bookId, BookDTO bookDTO) throws BookException {
        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                ()-> new BookException("Book with ID" + bookId + " not found.")
        );
        bookMapper.updateEntityFromDTO(bookDTO, existingBook);
        existingBook.isAvailableCopiesValid();
        Book updatedBook = bookRepository.save(existingBook);
        return bookMapper.toDTO(updatedBook);
    }

    @Override
    public void deleteBook(long bookId) throws BookException {
        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                ()-> new BookException("Book with ID" + bookId + " not found.")
        );
        existingBook.setActive(false);
        bookRepository.save(existingBook);
    }

    @Override
    public void hardDeleteBook(long bookId) throws BookException {
        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                ()-> new BookException("Book with ID" + bookId + " not found.")
        );
        bookRepository.delete(existingBook);
    }


    @Override
    public PageResponse<BookDTO> searchBooksWithFilters(BookSearchRequest searchRequest) {
        Pageable pageable = createPageable(searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortOrder());
        Page<Book> bookPage = bookRepository.searchBooksWithFilter(
                searchRequest.getSearchTerm(),
                searchRequest.getGenreId(),
                searchRequest.getAvailableOnly(),
                pageable
        );
        return convertToPageResponse(bookPage);
    }

    @Override
    public long getTotalActiveBooks() {
        return bookRepository.countByActiveTrue();
    }

    @Override
    public long getTotalAvailableBooks() {
        return bookRepository.countAvailableBooks();
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortOrder) {
        size = Math.min(size, 100);
        size = Math.max(size, 1);

        // Map Java field names to PostgreSQL column names (required for native query)
        String column = switch (sortBy) {
            case "createdAt"      -> "created_at";
            case "updatedAt"      -> "updated_at";
            case "availableCopies"-> "available_copies";
            case "totalCopies"    -> "total_copies";
            case "publishedDate"  -> "published_date";
            case "coverImageUrl"  -> "cover_image_url";
            default               -> sortBy;
        };

        Sort sort = sortOrder.equalsIgnoreCase("ASC")
                ? Sort.by(column).ascending()
                : Sort.by(column).descending();

        return PageRequest.of(page, size, sort);
    }

    private PageResponse<BookDTO> convertToPageResponse(Page<Book> books){
        List<BookDTO> bookDTOS = books.getContent()
                .stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(bookDTOS,
                books.getNumber(),
                books.getTotalPages(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isLast(),
                books.isFirst(),
                books.isEmpty());
    }
}
