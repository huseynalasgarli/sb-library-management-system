package com.hsyn.service;

import com.hsyn.payload.dto.BookDTO;
import com.hsyn.payload.request.BookSearchRequest;
import com.hsyn.payload.response.PageResponse;

import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);
    List<BookDTO> createBooksBulk();
    BookDTO getBookById(long bookId);
    BookDTO getBookByISBN(String isbn);
    BookDTO updateBook(long bookId,BookDTO bookDTO);
    void deleteBook(long bookId);
    void hardDeleteBook(long bookId);

    PageResponse<BookDTO> searchBooksWithFilters(
            BookSearchRequest searchRequest
    );

    long getTotalActiveBooks();

    long getTotalAvailableBooks();
}
