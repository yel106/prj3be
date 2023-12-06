package com.example.prj3be.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

//    @ManyToOne
//    @JoinColumn(name = "member_id")
//    private Member member;

    public Comment(Long id, String content, Board board) {
        this.id = id;
        this.content = content;
        this.board = board;
    }



}
