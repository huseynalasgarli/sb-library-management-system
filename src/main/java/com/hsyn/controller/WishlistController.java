package com.hsyn.controller;

import com.hsyn.payload.dto.WishlistDTO;
import com.hsyn.payload.response.ApiResponse;
import com.hsyn.payload.response.PageResponse;
import com.hsyn.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/add/{bookId}")
    public ResponseEntity<?> addWishlist(
            @PathVariable Long bookId,
            @RequestParam(required = false) String notes) throws Exception {
        WishlistDTO dto = wishlistService.addWishlist(bookId,notes);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<ApiResponse> removeFromWishlist(@PathVariable Long bookId) throws Exception {
        wishlistService.removeFromWishlist(bookId);
        return ResponseEntity.ok(
                new ApiResponse(
                        "Book removed from wishlist successfully",
                        true
                )
        );
    }

    @GetMapping("/my-wishlist")
    public ResponseEntity<?> getMyWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        PageResponse<WishlistDTO> dto = wishlistService
                .getMyWishlist(page, size);
        return ResponseEntity.ok(dto);

    }
}
