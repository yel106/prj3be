package com.example.prj3be.controller;

import com.example.prj3be.repository.ItemRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item")
public class ItemController {

    @PostMapping("add")
    public void method1(ItemRepository itemRepository){

    }

}
