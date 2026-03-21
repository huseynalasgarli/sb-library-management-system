package com.hsyn.payload.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreDTO {

    private Long id;

    @NotBlank(message = "Genre code is mandatory.")
    private String code;

    @NotBlank(message = "Genre name is mandatory.")
    private String genreName;

    @Size(max=500,message = "Description should not pass 500 characters.")
    private String description;

    @Min(value=0,message= "Display order cannot be negative.")
    private Integer displayOrder;

    private Boolean active;

    private Long parentGenreId;

    private String parentGenreName;

    private List<GenreDTO> subGenres;

    private Long bookCount;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;


}
