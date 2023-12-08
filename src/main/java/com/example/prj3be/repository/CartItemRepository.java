//package com.example.prj3be.repository;
//
//import com.example.prj3be.domain.CartItem;
//import com.example.prj3be.dto.CartInfoDto;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//
//public interface CartItemRepository extends JpaRepository<CartItem, Long> {
//    CartItem findByCartIdAndItemId(Long cart_id, Long itemId);
//
////    @Query("SELECT NEW com.example.prj3be.dto.CartInfoDto(ci.id, i.title, i.price, ci.count, i.imgUrl) " +
////            "FROM CartItem ci " +
////            "JOIN Item i ON ci.item.id = i.id " +
////            "WHERE ci.cart.id = :cart_id " +
////            "ORDER BY ci.regTime DESC")
////    List<CartInfoDto> findCartDetailDtoList(Long cart_id);
//
//
//}
