package com.example.prj3be.repository;

import com.example.prj3be.domain.CartItem;
import com.example.prj3be.dto.CartItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM Cart c JOIN CartItem ci WHERE c.id = :cartId AND ci.id = :itemId")
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.member.id = :memberId")
    void deleteByMemberId(Long memberId);

    List<CartItemDto> findCartDetailDtoList(Long id);
}
