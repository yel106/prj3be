package com.example.prj3be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( schema = "prj3")
@Getter @Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;
    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Comment(Long id, String content, Board board) {
        this.id = id;
        this.content = content;
        this.board = board;
    }

    public void setBoardId(Long id) {

    }
}
