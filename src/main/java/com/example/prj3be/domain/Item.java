package com.example.prj3be.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(schema = "prj3")
@NoArgsConstructor
@Getter @Setter
public class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    private String title;
    private String artist;

    @Enumerated(EnumType.STRING)
    private AlbumFormat albumFormat;

    private String price;
    private String agency;
    private LocalDate releaseDate;

    @OneToOne(mappedBy = "item")
    private Board board;

    public Item(Long id, String title, String artist, AlbumFormat albumFormat, String price, String agency, LocalDate releaseDate, Board board) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumFormat = albumFormat;
        this.price = price;
        this.agency = agency;
        this.releaseDate = releaseDate;
        this.board = board;
    }
}
