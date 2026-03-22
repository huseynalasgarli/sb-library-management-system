package com.hsyn.controller;

import com.hsyn.exception.GenreException;
import com.hsyn.model.Genre;
import com.hsyn.payload.dto.GenreDTO;
import com.hsyn.payload.response.ApiResponse;
import com.hsyn.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping()
    public ResponseEntity<?> getAllGenres(){
        List<GenreDTO> genres = genreService.getAllGenres();
        return ResponseEntity.ok().body(genres);
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<?> getGenreById(@RequestParam("genreId") long genreId) throws GenreException {
        GenreDTO genres = genreService.getGenreByID(genreId);
        return ResponseEntity.ok().body(genres);
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<?> updateGenre(@RequestParam("genreId") long genreId, @RequestBody GenreDTO genre) throws GenreException {
        GenreDTO updateGenre = genreService.updateGenre(genreId,genre);
        return ResponseEntity.ok().body(updateGenre);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<?> deleteGenre(@RequestParam("genreId") long genreId) throws GenreException {
        genreService.deleteGenre(genreId);
        ApiResponse apiResponse = new ApiResponse("Genre soft deleted successfully",true);
        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<?> hardDeleteGenre(@RequestParam("genreId") long genreId) throws GenreException {
        genreService.hardDeleteGenre(genreId);
        ApiResponse apiResponse = new ApiResponse("Genre hard deleted successfully.",true);
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<?> getTopLevelGenres(@RequestParam("genreId") long genreId) throws GenreException {
        GenreDTO genres = genreService.getGenreByID(genreId);
        return ResponseEntity.ok().body(genres);
    }



}
