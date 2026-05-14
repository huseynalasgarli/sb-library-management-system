package com.hsyn.repository;

import com.hsyn.model.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist,Long> {
    Page<Wishlist> findByUserId(Long userId, Pageable pageable);
    boolean existsByUserIdAndBookId(Long userId,Long bookId);
    Wishlist findByUserIdAndBookId(Long userId,Long bookId);
}
