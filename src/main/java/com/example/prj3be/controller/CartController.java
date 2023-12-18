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

    @PostMapping("/fetch")
    public List<CartItemDto> fetchCart() {
        System.out.println("CartController.fetchCart");
        //accessToken으로부터 로그인 아이디 추출
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        //로그인 아이디로부터 멤버 아이디 추출
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        return cartService.getCartList(memberId);
    }

    @PostMapping("/add")
    public ResponseEntity createCartAndAddItem(Long boardId, String title, Double price) {
        //id = board.id (상품명)
        System.out.println("boardId = " + boardId);
        System.out.println("CartController.createCartAndAddItem");
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        try {
            Cart cart = cartService.createCart(memberId);
            System.out.println("CartController에서 cart = " + cart.getId());
            cartService.addItemsToCart(cart, boardId, title, price);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}