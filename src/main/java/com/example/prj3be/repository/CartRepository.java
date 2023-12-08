package com.example.prj3be.repository;

import com.example.prj3be.domain.Cart;
//import com.example.prj3be.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByMemberId(Long memberId);
}
