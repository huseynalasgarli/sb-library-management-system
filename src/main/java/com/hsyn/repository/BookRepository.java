package com.hsyn.repository;

import com.hsyn.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Query(value = """
            SELECT * FROM book
            WHERE active = true
            AND (
                :searchTerm IS NULL OR
                lower(title) LIKE lower('%' || CAST(:searchTerm AS text) || '%') OR
                lower(author) LIKE lower('%' || CAST(:searchTerm AS text) || '%') OR
                lower(isbn) LIKE lower('%' || CAST(:searchTerm AS text) || '%')
            )
            AND (:genreId IS NULL OR genre_id = :genreId)
            AND (:availableOnly = false OR available_copies > 0)
            """,
            countQuery = """
            SELECT COUNT(*) FROM book
            WHERE active = true
            AND (
                :searchTerm IS NULL OR
                lower(title) LIKE lower('%' || CAST(:searchTerm AS text) || '%') OR
                lower(author) LIKE lower('%' || CAST(:searchTerm AS text) || '%') OR
                lower(isbn) LIKE lower('%' || CAST(:searchTerm AS text) || '%')
            )
            AND (:genreId IS NULL OR genre_id = :genreId)
            AND (:availableOnly = false OR available_copies > 0)
            """,
            nativeQuery = true)
    Page<Book> searchBooksWithFilter(
            @Param("searchTerm") String searchTerm,
            @Param("genreId") Long genreId,
            @Param("availableOnly") boolean availableOnly,
            Pageable pageable
    );

    long countByActiveTrue();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.availableCopies > 0 AND b.active = true")
    long countAvailableBooks();
}