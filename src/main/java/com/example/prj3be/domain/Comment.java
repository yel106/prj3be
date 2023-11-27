package com.example.prj3be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prj3")
@Getter @Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Comment(Long id, Board board) {
        this.id = id;
        this.board = board;
    }
}
