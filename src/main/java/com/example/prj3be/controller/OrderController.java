package com.example.prj3be.controller;

import com.example.prj3be.dto.OrderFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    @PostMapping
    public void order(@Validated @RequestBody OrderFormDto dto){
        //Order order = new Order();

        System.out.println("dto.getName() = " + dto.getName());
        System.out.println("dto.getAddr() = " + dto.getAddr());
        System.out.println("dto.getPhone() = " + dto.getPhone());
        System.out.println("dto.getTotalPrice() = " + dto.getTotalPrice());
        System.out.println("dto.getPayment() = " + dto.getPayment());
    }
}
