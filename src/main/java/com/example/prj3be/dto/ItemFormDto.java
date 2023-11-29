package com.example.prj3be.dto;

import com.example.prj3be.domain.AlbumFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ItemFormDto {

    @NotBlank(message = "Register Album Title")
    private String Title;
    @NotBlank(message = "Input the Artist's Name")
    private String Artist;
    @NotBlank(message = "What is the release date of the album?")
    private LocalDate ReleaseDate;
    @NotBlank(message = "Where is the album released?")
    private String Agency;
    @NotBlank(message = "What is the type of album?")
    private AlbumFormat AlbumFormat;
    @NotBlank(message = "Register Price of product")
    private String Price;
}


