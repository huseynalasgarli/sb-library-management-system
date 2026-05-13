package com.hsyn.service.impl;

import com.hsyn.model.Book;
import com.hsyn.model.User;
import com.hsyn.payload.dto.WishlistDTO;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.repository.BookRepository;
import com.hsyn.repository.WishlistRepository;
import com.hsyn.service.UserService;
import com.hsyn.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final BookRepository bookRepository;

    @Override
    public WishlistDTO addWishlist(Long bookId) throws Exception {
        User user = userService.getCurrentUser();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new Exception("Book not found"));
        return null;
    }

    @Override
    public void removeFromWishlist(Long bookId) {

    }

    @Override
    public PageResponse<WishlistDTO> getMyWishlists(int page, int size) {
        return null;
    }
}
