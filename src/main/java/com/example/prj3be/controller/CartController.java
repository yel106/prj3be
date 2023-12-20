package com.example.prj3be.controller;

import com.example.prj3be.domain.Cart;
import com.example.prj3be.dto.CartItemDto;
import com.example.prj3be.exception.OutOfStockException;
import com.example.prj3be.repository.MemberRepository;
import com.example.prj3be.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final MemberRepository memberRepository;

    @GetMapping("/fetch")
    public List<CartItemDto> fetchCart() {
        System.out.println("CartController.fetchCart");
        //accessToken으로부터 로그인 아이디 추출
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("===========================================================");
        System.out.println("logId = " + logId);
        //로그인 아이디로부터 멤버 아이디 추출
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        return cartService.getCartList(memberId);
    }

    @PostMapping("/add")
    public ResponseEntity createCartAndAddItem(Long boardId, Long stockQuantity) {
        //id = board.id (상품명)
        System.out.println("boardId = " + boardId);
        System.out.println("CartController.createCartAndAddItem");
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);
        System.out.println("stockQuantity = " + stockQuantity);

        try {
            Cart cart = cartService.createCart(memberId);
            System.out.println("CartController에서 cart = " + cart.getId());
            cartService.addItemsToCart(cart, boardId, stockQuantity);
            return ResponseEntity.ok().build();
        } catch (OutOfStockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemId) {
        System.out.println("CartController.deleteCartItem");
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        try {
            cartService.deleteCartItemByCartAndCartItem(memberId, cartItemId);
            return ResponseEntity.ok(cartItemId + "번 아이템 삭제 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카트 아이템 삭제 중 오류 발생");
        }
    }

    @GetMapping("/addCount/{cartItemId}")
    public void addCount(@PathVariable Long cartItemId) {
        System.out.println("========================================");
        System.out.println("CartController.addCount");
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        //로그인 아이디로부터 멤버 아이디 추출
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        cartService.addCountToCartItem(memberId, cartItemId);
        System.out.println("========================================");
    }

    @GetMapping("/subtractCount/{cartItemId}")
    public void subtractCount(@PathVariable Long cartItemId) {
        System.out.println("========================================");
        System.out.println("CartController.subtractCount");
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        //로그인 아이디로부터 멤버 아이디 추출
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        cartService.subtractCountFromCartItem(memberId, cartItemId);
        System.out.println("========================================");
    }


}