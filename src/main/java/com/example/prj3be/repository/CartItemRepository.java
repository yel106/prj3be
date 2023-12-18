package com.example.prj3be.repository;

import com.example.prj3be.domain.CartItem;
import com.example.prj3be.dto.CartItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    //TODO: 고치삼
    @Query("SELECT ci FROM Cart c JOIN CartItem ci WHERE c.id = :cartId AND ci.id = :itemId")
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.member.id = :memberId")
    void deleteByMemberId(Long memberId);

    @Query("SELECT new com.example.prj3be.dto.CartItemDto(ci.id, ci.title, ci.price, ci.count, ci.fileUrl) FROM CartItem ci WHERE ci.cart.id = :cartId")
    List<CartItemDto> findCartDetailDtoList(Long cartId);
}
