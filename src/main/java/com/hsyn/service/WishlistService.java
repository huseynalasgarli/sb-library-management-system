package com.hsyn.service;

import com.hsyn.payload.dto.WishlistDTO;
import com.hsyn.payload.response.PageResponse;
import org.springframework.data.domain.PageRequest;

public interface WishlistService {

    WishlistDTO addWishlist(Long bookId) throws Exception;
    void removeFromWishlist(Long bookId);
    PageResponse<WishlistDTO> getMyWishlists(int page,int size);
}
