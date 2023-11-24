package com.example.prj3be.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "prj3")
@NoArgsConstructor
@Getter @Setter
public class Board {
    @Id  @GeneratedValue
    @Column(name = "board_id")
    private Long id;
    private String content;

    @OneToMany(mappedBy = "board")
    private List<Item> items = new ArrayList<>();


    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

    public Board(Long id, String content, List<Item> items, List<Comment> comments) {
        this.id = id;
        this.content = content;
        this.items = items;
        this.comments = comments;
    }
}
