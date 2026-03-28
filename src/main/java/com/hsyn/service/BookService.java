package com.hsyn.service;

import com.hsyn.exception.BookException;
import com.hsyn.payload.dto.BookDTO;
import com.hsyn.payload.request.BookSearchRequest;
import com.hsyn.payload.response.PageResponse;

import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO) throws BookException;
    List<BookDTO> createBooksBulk(List<BookDTO> bookDTOS) throws BookException;
    BookDTO getBookById(long bookId) throws BookException;
    BookDTO getBookByISBN(String isbn) throws BookException;
    BookDTO updateBook(long bookId,BookDTO bookDTO) throws BookException;
    void deleteBook(long bookId) throws BookException;
    void hardDeleteBook(long bookId) throws BookException;

    PageResponse<BookDTO> searchBooksWithFilters(
            BookSearchRequest searchRequest
    );

    long getTotalActiveBooks();

    long getTotalAvailableBooks();
}
