package com.hsyn.service.impl;

import com.hsyn.mapper.WishlistMapper;
import com.hsyn.model.Book;
import com.hsyn.model.User;
import com.hsyn.model.Wishlist;
import com.hsyn.payload.dto.WishlistDTO;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.repository.BookRepository;
import com.hsyn.repository.WishlistRepository;
import com.hsyn.service.UserService;
import com.hsyn.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    public WishlistDTO addWishlist(Long bookId,String notes) throws Exception {
        User user = userService.getCurrentUser();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new Exception("Book not found"));

        if (wishlistRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new Exception("Book is already in your wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setBook(book);
        wishlist.setNotes(notes);
        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return wishlistMapper.toDTO(savedWishlist);
    }

    @Override
    public void removeFromWishlist(Long bookId) throws Exception {
        User user = userService.getCurrentUser();
        Wishlist wishlist = wishlistRepository.findByUserIdAndBookId(user.getId(), bookId);
        if (wishlist == null) {
            throw new Exception("Book is not in your wishlist");
        }
        wishlistRepository.delete(wishlist);
    }

    @Override
    public PageResponse<WishlistDTO> getMyWishlist(int page, int size) {

        Long userId = userService.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("addedAt").descending()
        );

        Page<Wishlist> wishlistPage =
                wishlistRepository.findByUserId(userId, pageable);

        return convertToPageResponse(wishlistPage);
    }

    private PageResponse<WishlistDTO> convertToPageResponse(
            Page<Wishlist> wishlistPage
    ) {

        List<WishlistDTO> wishlistDTOs = wishlistPage.getContent()
                .stream()
                .map(wishlistMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                wishlistDTOs,
                wishlistPage.getNumber(),
                wishlistPage.getSize(),
                wishlistPage.getTotalElements(),
                wishlistPage.getTotalPages(),
                wishlistPage.isLast(),
                wishlistPage.isFirst(),
                wishlistPage.isEmpty()
        );
    }
}
