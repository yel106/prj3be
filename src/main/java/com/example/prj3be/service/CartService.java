package com.example.prj3be.service;

import com.example.prj3be.domain.Cart;
import com.example.prj3be.domain.CartItem;
import com.example.prj3be.domain.Member;
import com.example.prj3be.dto.CartItemDto;
import com.example.prj3be.repository.BoardFileRepository;
import com.example.prj3be.repository.CartItemRepository;
import com.example.prj3be.repository.CartRepository;
import com.example.prj3be.repository.MemberRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final BoardFileRepository boardFileRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;

    public Cart createCart(Long memberId) {
        System.out.println("CartService.createCart");
        Member member = memberRepository.findByMemberId(memberId);
        System.out.println("member = " + member.getId());
        Cart cart = cartRepository.findByMemberId(memberId);
        System.out.println("!= 체크하기 전에 cart = " + cart);

        // 해당 회원의 카트가 없으면 만듦, 저장
        if(cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        return cart;
    }

    public void addItemsToCart(Cart cart, Long boardId, String title, Double price) {
        System.out.println("CartService.addItemsToCart");
        System.out.println("boardId = " + boardId);
        String fileUrl = boardFileRepository.findFileUrlsByBoardId(boardId).get(0);
        System.out.println("fileUrl = " + fileUrl);
        int count = 1;
        System.out.println("count = " + count);
        System.out.println("cart.getId() = " + cart.getId());

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), boardId);
        System.out.println("savedCartItem = " + savedCartItem);

        //만약 장바구니에 해당 아이템이 이미 존재할 경우 수량 증대
        if(savedCartItem != null) {
            System.out.println("장바구니에 해당 아이템이 존재함");
            savedCartItem.addCount(count);
            System.out.println("savedCartItem = " + savedCartItem.getCount());
        } else {
            System.out.println("장바구니에 해당 아이템이 존재하지 않음");
            //만약 장바구니에 해당 아이템이 존재하지 않을 경우 추가
            CartItem cartItem = CartItem.createCartItem(cart, boardId, title, price, fileUrl, count);
            System.out.println("cartItem = " + cartItem.getId() + "번 제품");
            System.out.println("cartItem.getCart() = " + cartItem.getCart());
            System.out.println("cartItem.getTitle() = " + cartItem.getTitle());
            System.out.println("cartItem.getCount() = " + cartItem.getCount());
            System.out.println("cartItem.getPrice() = " + cartItem.getPrice());
            cartItemRepository.save(cartItem);
        }
    }

    public List<CartItemDto> getCartList(Long id) {
        List<CartItemDto> cartItemList = new ArrayList<>();

        Cart cart = cartRepository.findByMemberId(id);
        if(cart == null) {
            return cartItemList;
        }

        cartItemList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartItemList;
    }

    public void deleteCartItem(Long id) {
        cartItemRepository.deleteByMemberId(id);
    }

    public void deleteCart(Long id) {
        cartRepository.deleteByMemberId(id);
    }

}
