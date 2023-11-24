package com.example.prj3be.repository;

import com.example.prj3be.domain.CartItem;
import com.example.prj3be.dto.CartInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    //TODO : item 쪽 완성되면 칼럼명과 테이블 구조 따라 쿼리문 수정
    //TODO : 아이템 이미지 불러올 때 이미지가 있는지 없는지 여부 체크하는 논리 부분 추가하기
//    @Query("select new com.example.prj3be.dto(ci.id, i.itemName, i.price, ci.count, im.imgUrl)" +
//            "from CartItem ci, ItemImg im " +
//            "join ci.item i " +
//            "where ci.cart.id = :cartId " +
//            "and im.item.id = ci.item.id " +
//            "order by ci.regTime desc"
//    )
//    List<CartInfoDto> findCartDetailDtoList(Long cartId);

}
