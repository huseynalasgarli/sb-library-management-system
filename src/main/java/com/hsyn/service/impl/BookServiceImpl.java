package com.hsyn.service.impl;

import com.hsyn.payload.dto.BookDTO;
import com.hsyn.payload.request.BookSearchRequest;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        return null;
    }

    @Override
    public List<BookDTO> createBooksBulk() {
        return List.of();
    }

    @Override
    public BookDTO getBookById(long bookId) {
        return null;
    }

    @Override
    public BookDTO getBookByISBN(String isbn) {
        return null;
    }

    @Override
    public BookDTO updateBook(long bookId, BookDTO bookDTO) {
        return null;
    }

    @Override
    public void deleteBook(long bookId) {

    }

    @Override
    public void hardDeleteBook(long bookId) {

    }

    @Override
    public PageResponse<BookDTO> searchBooksWithFilters(BookSearchRequest searchRequest) {
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
}
