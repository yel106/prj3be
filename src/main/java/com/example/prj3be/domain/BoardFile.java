package com.example.prj3be.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (schema = "prj3")
@NoArgsConstructor
@Getter @Setter
public class BoardFile {
    @Id
    @GeneratedValue
    @Column(name = "boardFile_id")
    private Long id;
    private String fileName;
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public BoardFile(Long id, String fileName, String fileUrl) {
        this.id = id;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
