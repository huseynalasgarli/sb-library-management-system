package com.hsyn.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Genre code is mandatory.")
    private String code;

    @NotBlank(message = "Genre name is mandatory.")
    private String genreName;

    @Size(max=500,message = "Description should not pass 500 characters.")
    private String description;

    @Min(value=0,message= "Display order cannot be negative.")
    private Integer displayOrder;

    @Column(nullable = false)
    private Boolean active=true;

    @ManyToOne
    private Genre parentGenre;

    @OneToMany
    private List<Genre> subGenres = new ArrayList<Genre>();

//    @OneToMany(mappedBy = "genre",cascade = CascadeType.PERSIST)
//    private List<Book> books = new ArrayList<Book>();

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

}
