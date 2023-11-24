package com.example.prj3be.service;

import com.example.prj3be.domain.Cart;
import com.example.prj3be.domain.CartItem;
import com.example.prj3be.domain.Member;
import com.example.prj3be.dto.CartInfoDto;
import com.example.prj3be.dto.CartItemDto;
import com.example.prj3be.repository.CartItemRepository;
import com.example.prj3be.repository.CartRepository;
import com.example.prj3be.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
//
//    private final ItemRepository itemRepository;
//    private final MemberRepository memberRepository;
//    private final CartRepository cartRepository;
//    private final CartItemRepository cartItemRepository;
//
//    public Long addCart(CartItemDto cartItemDto, Integer id) {
//        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
//        Member member = memberRepository.findByEmail(email);
//
//        Cart cart = cartRepository.findByMemberId(member.getId());
//        if(cart != null) {
//            cart = Cart.createCart(member);
//            cartRepository.save(cart);
//        }
//
//        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
//
//        if(savedCartItem != null) { //만약 장바구니에 해당 아이템이 이미 존재할 경우 수량 증대
//            savedCartItem.addCount(cartItemDto.getCount());
//            return savedCartItem.getId();
//        } else { //만약 장바구니에 해당 아이템이 존재하지 않을 경우 추가
//            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
//            cartItemRepository.save(cartItem);
//            return cartItem.getId();
//        }
//    }
//
//    @Transactional(readOnly = true)
//    public List<CartInfoDto> getCartList(String email) {
//        List<CartInfoDto> cartInfoDtoList = new ArrayList<>();
//
//        Member member = memberRepository.findByEmail(email);
//        Cart cart = cartRepository.findByMemberId(member.getId());
//        if(cart == null) {
//            return cartInfoDtoList;
//        }
//
//        cartInfoDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
//
//        return cartInfoDtoList;
//    }
}
