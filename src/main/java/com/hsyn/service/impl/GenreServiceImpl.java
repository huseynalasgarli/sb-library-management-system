package com.hsyn.service.impl;


import com.hsyn.model.Genre;
import com.hsyn.payload.dto.GenreDTO;
import com.hsyn.repository.GenreRepository;
import com.hsyn.service.GenreService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public GenreDTO createGenre(GenreDTO genreDTO) {
//        return genreRepository.save(genreDTO);
        Genre genre = Genre.builder()
                .code(genreDTO.getCode())
                .genreName(genreDTO.getGenreName())
                .description(genreDTO.getDescription())
                .displayOrder(genreDTO.getDisplayOrder())
                .active(true)
                .build();

        if (genreDTO.getParentGenreId() != null) {
            Genre  parentGenre = genreRepository.findById(genreDTO.getParentGenreId()).get();
            genre.setParentGenre(parentGenre);
        }
        Genre savedGenre = genreRepository.save(genre);

        GenreDTO dto = new  GenreDTO().builder()
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

//        dto.setSubGenres(savedGenre.getSubGenres().stream()
//                .filter(subGenre -> subGenre.getActive())
//                .map(subGenre -> ));


//        dto.setBookCount((long) (savedGenre.get));
        return dto;
    }
}
