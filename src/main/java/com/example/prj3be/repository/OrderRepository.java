package com.example.prj3be.repository;

import com.example.prj3be.domain.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("SELECT o.orderName FROM Order o WHERE o.member.id = :id")
    List<String> findOrderNamesByMemberLogId(@Param("id") Long id);

}
