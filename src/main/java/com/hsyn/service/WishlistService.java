package com.hsyn.service;

import com.hsyn.payload.dto.WishlistDTO;
import com.hsyn.payload.response.PageResponse;

public interface WishlistService {

    WishlistDTO addWishlist(Long bookId,String notes) throws Exception;
    void removeFromWishlist(Long bookId) throws Exception;
    PageResponse<WishlistDTO> getMyWishlist(int page,int size);
}
