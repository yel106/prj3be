package com.example.prj3be.controller;

import com.example.prj3be.domain.Item;
import com.example.prj3be.dto.ItemFormDto;
import com.example.prj3be.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemController {
    private final ItemService itemService;

    @PostMapping("addItem")
    public void method1(@Validated @RequestBody ItemFormDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setTitle(dto.getTitle());
        item.setArtist(dto.getArtist());
        item.setReleaseDate(dto.getReleaseDate());
        item.setAlbumFormat(dto.getAlbumFormat());
        item.setAgency(dto.getAgency());
        item.setPrice(dto.getPrice());
    }
}
