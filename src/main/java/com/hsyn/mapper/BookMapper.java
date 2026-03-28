package com.hsyn.mapper;

import com.hsyn.exception.BookException;
import com.hsyn.model.Book;
import com.hsyn.model.Genre;
import com.hsyn.payload.dto.BookDTO;
import com.hsyn.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final GenreRepository genreRepository;

    public BookDTO toDTO(Book book){
        if(book==null){
            return null;
        }

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .genreId(book.getGenre().getId())
                .genreName(book.getGenre().getGenreName())
                .genreCode(book.getGenre().getCode())
                .publisher(book.getPublisher())
                .publicationDate(book.getPublishedDate())
                .language(book.getLanguage())
                .pages(book.getPages())
                .description(book.getDescription())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .price(book.getPrice())
                .coverImageUrl(book.getCoverImageUrl())
                .active(book.getActive())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public Book toEntity(BookDTO bookDTO) throws BookException {
        if(bookDTO==null){
            return null;
        }

        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setIsbn(bookDTO.getIsbn());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());




        if(bookDTO.getGenreId()!=null){
            book.setGenre(findGenreById(bookDTO.getGenreId()));
        }

        book.setPublisher(bookDTO.getPublisher());
        book.setPublishedDate(bookDTO.getPublicationDate());
        book.setLanguage(bookDTO.getLanguage());
        book.setPages(bookDTO.getPages());
        book.setDescription(bookDTO.getDescription());
        book.setTotalCopies(bookDTO.getTotalCopies());
        book.setAvailableCopies(bookDTO.getAvailableCopies());
        book.setPrice(bookDTO.getPrice());
        book.setCoverImageUrl(bookDTO.getCoverImageUrl());
        book.setActive(true);

        return book;
    }

    public void updateEntityFromDTO(BookDTO bookDTO, Book book) throws BookException {
        if(bookDTO==null || book==null){
            return;
        }
        //ISBN should not be updated
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());

        if(bookDTO.getGenreId()!=null){
            book.setGenre(findGenreById(bookDTO.getGenreId()));
        }

        book.setPublisher(bookDTO.getPublisher());
        book.setPublishedDate(bookDTO.getPublicationDate());
        book.setLanguage(bookDTO.getLanguage());
        book.setPages(bookDTO.getPages());
        book.setDescription(bookDTO.getDescription());
        book.setTotalCopies(bookDTO.getTotalCopies());
        book.setAvailableCopies(bookDTO.getAvailableCopies());
        book.setPrice(bookDTO.getPrice());
        book.setCoverImageUrl(bookDTO.getCoverImageUrl());

        if(bookDTO.getActive()!=null){
            book.setActive(bookDTO.getActive());
        }
    }

    private Genre findGenreById(Long genreId) throws BookException {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new BookException("Genre with ID " + genreId + " not found."));
    }
}
