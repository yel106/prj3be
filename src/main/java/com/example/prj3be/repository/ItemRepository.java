package com.example.prj3be.repository;

import com.example.prj3be.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

}