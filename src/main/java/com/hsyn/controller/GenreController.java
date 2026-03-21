package com.hsyn.controller;

import com.hsyn.model.Genre;
import com.hsyn.payload.dto.GenreDTO;
import com.hsyn.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @PostMapping("/create")
    public ResponseEntity<GenreDTO> addGenre(@RequestBody GenreDTO genreDTO){
        GenreDTO createdGenre = genreService.createGenre(genreDTO);
        return ResponseEntity.ok().body(createdGenre);
     }
}
