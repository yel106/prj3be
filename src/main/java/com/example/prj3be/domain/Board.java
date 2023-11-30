package com.example.prj3be.domain;


import  jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank private String title;
    @NotBlank private String price;

    @OneToOne
    @JoinColumn(name = "item_id") //item_id를 외래키로 사용. item_id가 Board의 pk를 참조
    private Item item;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();



    public Board(Long id, String title, String price, String image, Item item, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.item = item;
        this.comments = comments;
    }
}
