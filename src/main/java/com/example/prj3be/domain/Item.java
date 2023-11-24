package com.example.prj3be.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String releasedDate;
// title 앨범 명, 가수명, releasedDate : 발매일 ,
    @Enumerated(EnumType.STRING)
    private AlbumFormat albumFormat;

    private String agency;
    private String price;
    private int stockQuantity; //재고 수량

//    private LocalDate releaseDate; ---> 발매일자 : local date?????

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")      //board_id를 외래키로 사용. board_id가 Item의 pk를 참조
    private Board board;

    public Item(Long id, Board board) {
        this.id = id;
        this.board = board;

    }
}
