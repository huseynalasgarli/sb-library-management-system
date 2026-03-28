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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        return null;
    }

    @Override
    public long getTotalActiveBooks() {
        return 0;
    }

    @Override
    public long getTotalAvailableBooks() {
        return 0;
    }

    private Pageable createPageable(int page, int size,String sortBy,String sortOrder) {
         size= Math.min(size,10);
         size = Math.max(size,1);

         Sort sort = sortOrder.equalsIgnoreCase("ASC")
                 ? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
         return PageRequest.of(page, size, sort);
    }
}
