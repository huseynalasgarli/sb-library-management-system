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
    public ResponseEntity<?> getGenreById(@PathVariable("genreId") long genreId) throws GenreException {
        GenreDTO genres = genreService.getGenreByID(genreId);
        return ResponseEntity.ok().body(genres);
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<?> updateGenre(@PathVariable("genreId") long genreId, @RequestBody GenreDTO genre) throws GenreException {
        GenreDTO updateGenre = genreService.updateGenre(genreId,genre);
        return ResponseEntity.ok().body(updateGenre);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<?> deleteGenre(@PathVariable("genreId") long genreId) throws GenreException {
        genreService.deleteGenre(genreId);
        ApiResponse apiResponse = new ApiResponse("Genre soft deleted successfully",true);
        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{genreId}/hard")
    public ResponseEntity<?> hardDeleteGenre(@PathVariable("genreId") long genreId) throws GenreException {
        genreService.hardDeleteGenre(genreId);
        ApiResponse apiResponse = new ApiResponse("Genre hard deleted successfully.",true);
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/top-level")
    public ResponseEntity<?> getTopLevelGenres() throws GenreException {
        List<GenreDTO> topLevelGenres = genreService.getTopLevelGenres();
        return ResponseEntity.ok().body(topLevelGenres);
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTotalActiveGenres() throws GenreException {
        Long totalActiveGenres = genreService.getTotalActiveGenres();
        return ResponseEntity.ok().body(totalActiveGenres);
    }

    @GetMapping("/{id}/book-count")
    public ResponseEntity<?> getBookCountByGenres(@PathVariable Long id) throws GenreException {
        Long count = genreService.getBookCountByGenre(id);

        return ResponseEntity.ok().body(count);
    }


}
