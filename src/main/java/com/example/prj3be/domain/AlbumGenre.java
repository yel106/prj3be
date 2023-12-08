package com.example.prj3be.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "prj3")
@Getter
@Setter
public class AlbumGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "albumGenre_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlbumDetail albumDetail;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
}
