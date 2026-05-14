package com.hsyn.service;

import com.hsyn.payload.dto.BookReviewDTO;
import com.hsyn.payload.request.CreateReviewRequest;
import com.hsyn.payload.request.UpdateReviewRequest;
import com.hsyn.payload.response.PageResponse;

public interface BookReviewService {

    BookReviewDTO createReview(CreateReviewRequest request) throws Exception;

    BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) throws Exception;

    void deleteReview(Long reviewId) throws Exception;

    PageResponse<BookReviewDTO> getReviewsByBookId(Long id, int page, int size) throws Exception;
}
