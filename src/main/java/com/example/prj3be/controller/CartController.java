package com.example.prj3be.controller;

import com.example.prj3be.domain.Cart;
import com.example.prj3be.dto.CartItemDto;
import com.example.prj3be.repository.MemberRepository;
import com.example.prj3be.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final MemberRepository memberRepository;

    @PostMapping
    public List<CartItemDto> fetchCart() {
        //accessToken으로부터 로그인 아이디 추출
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        //로그인 아이디로부터 멤버 아이디 추출
        Long memberId = memberRepository.findIdByLogId(logId);

        return cartService.getCartList(memberId);
    }

    @PostMapping("/add")
    public ResponseEntity createCartAndAddItem(Long id, String title, Double price) {
        //id = board.id (상품명)
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long memberId = memberRepository.findIdByLogId(logId);

        Long cartItemId;

        try {
            Cart cart = cartService.createCart(memberId);
            cartService.addItemsToCart(cart, id, title, price);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}