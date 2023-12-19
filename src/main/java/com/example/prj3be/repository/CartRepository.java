package com.example.prj3be.repository;

import com.example.prj3be.domain.Cart;
//import com.example.prj3be.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.member.id = :memberId")
    Cart findByMemberId(Long memberId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.member.id = :memberId")
    void deleteByMemberId(Long memberId);
}
