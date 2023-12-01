package com.example.prj3be.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

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
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;
    private String title;
    private String artist;

    @Enumerated(EnumType.STRING)
    private AlbumFormat albumFormat;

    private String price;
    private String agency;
    private LocalDate releaseDate;
    private Long stockQuantity;

    @NotBlank(message = "Image URL cannot be blank")
    @Column(name="image_url")
    private String imageURL;
    private String fileName;

//    @OneToOne
//    @JoinColumn(name = "item_id") //item_id를 외래키로 사용. item_id가 Board의 pk를 참조
//    private Item item;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();
    private String imageUrl;


    public Board(Long id, String title, String artist, AlbumFormat albumFormat, String price, String agency, String imageURL, LocalDate releaseDate, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumFormat = albumFormat;
        this.price = price;
        this.agency = agency;
        this.releaseDate = releaseDate;
        this.comments = comments;
        this.imageURL = imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageUrl = imageURL;
    }

    public String getFileName() {
        return this.fileName;
    }
}
