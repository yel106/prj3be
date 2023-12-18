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
        Member member = memberRepository.findByMemberId(memberId);
        Cart cart = cartRepository.findByMemberId(memberId);

        // 해당 회원의 카트가 없으면 만듦, 저장
        if(cart != null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        return cart;
    }

    public Long addItemsToCart(Cart cart, Long itemId, String title, Double price) {
        String fileUrl = boardFileRepository.findFileUrlsByBoardId(itemId).get(0);
        int count = 1;

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), itemId);

        //만약 장바구니에 해당 아이템이 이미 존재할 경우 수량 증대
        if(savedCartItem != null) {
            savedCartItem.addCount(count);
            return savedCartItem.getId();
        } else {
            //만약 장바구니에 해당 아이템이 존재하지 않을 경우 추가
            CartItem cartItem = CartItem.createCartItem(cart, itemId, title, price, fileUrl, count);
            cartItemRepository.save(cartItem);
            return cartItem.getId();
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
