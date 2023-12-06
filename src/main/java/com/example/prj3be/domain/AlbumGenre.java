package com.example.prj3be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "prj3")
@Getter
@Setter
public class AlbumGenre {
    @Id
    @GeneratedValue
    private Long id;

    //    private String genreName;
    @Enumerated(EnumType.STRING)
    private AlbumDetail albumDetail;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
}
