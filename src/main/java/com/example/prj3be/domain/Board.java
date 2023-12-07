package com.example.prj3be.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "prj3")
@NoArgsConstructor
@Getter
@Setter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;
    private String title;
    private String artist;

    @Enumerated(EnumType.STRING)
    private AlbumFormat albumFormat;

//    @Enumerated(EnumType.STRING)
//    private AlbumDetail albumDetail;

    private String price;
    private String agency;
    private LocalDate releaseDate;
    private Long stockQuantity;


    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private List<AlbumGenre> albumGenres = new ArrayList<>();


    public Board(Long id, String title, String artist, AlbumFormat albumFormat, String price, String agency, LocalDate releaseDate, Long stockQuantity, List<Comment> comments, List<AlbumGenre> albumGenres) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumFormat = albumFormat;
        this.price = price;
        this.agency = agency;
        this.releaseDate = releaseDate;
        this.stockQuantity = stockQuantity;
        this.comments = comments;
        this.albumGenres = albumGenres;
    }
}
