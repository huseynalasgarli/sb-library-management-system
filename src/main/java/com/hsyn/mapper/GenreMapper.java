package com.hsyn.mapper;

import com.hsyn.model.Genre;
import com.hsyn.payload.dto.GenreDTO;
import com.hsyn.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreMapper {

    private final GenreRepository genreRepository;

    public GenreDTO toDTO(Genre savedGenre) {

        if(savedGenre == null) return null;

        GenreDTO dto = new GenreDTO().builder()
                .id(savedGenre.getId())
                .code(savedGenre.getCode())
                .genreName(savedGenre.getGenreName())
                .description(savedGenre.getDescription())
                .displayOrder(savedGenre.getDisplayOrder())
                .active(savedGenre.getActive())
                .createdDate(savedGenre.getCreatedDate())
                .updatedDate(savedGenre.getUpdatedDate())
                .build();

        if(savedGenre.getParentGenre() != null) {
            dto.setParentGenreId(savedGenre.getParentGenre().getId());
            dto.setParentGenreName(savedGenre.getParentGenre().getGenreName());
        }

        if (savedGenre.getSubGenres() != null && !savedGenre.getSubGenres().isEmpty()) {
            dto.setSubGenres(savedGenre.getSubGenres().stream()
                    .filter(subGenre -> subGenre.getActive())
                    .map(subGenre -> toDTO(subGenre)).collect(Collectors.toList()));
        }

//        dto.setBookCount((long) (savedGenre.get));
        return dto;
    }

    public Genre toEntity(GenreDTO genreDTO) {
        if(genreDTO == null) return null;

        Genre genre = Genre.builder()
                .code(genreDTO.getCode())
                .genreName(genreDTO.getGenreName())
                .description(genreDTO.getDescription())
                .displayOrder(genreDTO.getDisplayOrder())
                .active(true)
                .build();

        if (genreDTO.getParentGenreId() != null) {
            genreRepository.findById(genreDTO.getParentGenreId())
                    .ifPresent(
                    genre::setParentGenre
            );
        }

        return genre;
    }

    public void updateEntityFromDTO(GenreDTO genreDTO, Genre genre) {
        if(genre == null || genreDTO == null) return;

        genre.setCode(genreDTO.getCode());
        genre.setGenreName(genreDTO.getGenreName());
        genre.setDescription(genreDTO.getDescription());
        genre.setDisplayOrder(genreDTO.getDisplayOrder() != null ? genreDTO.getDisplayOrder() : 0 );
        genre.setActive(genreDTO.getActive() );
        if (genreDTO.getActive() != null){
            genre.setActive(genreDTO.getActive());
        }
        if (genreDTO.getParentGenreId() != null) {
            genreRepository.findById(genreDTO.getParentGenreId())
                    .ifPresent(genre::setParentGenre);
        }


    }

    public List<GenreDTO> toDTOList(List<Genre> genreList){
        return genreList.stream().map(genre -> toDTO(genre)).collect(Collectors.toList());
    }
}
