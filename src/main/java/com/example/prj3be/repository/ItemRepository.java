package com.example.prj3be.repository;

import com.example.prj3be.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.id = :itemId")
    Optional<Item> findItemsById(@Param("itemId")Long itemId);
    @Query("SELECT i FROM Item i WHERE i.title = :itemTitle")
    List<Item> findItemsByName(@Param("itemTitle") String itemTitle);




//    {/*@param어노테이션 사용해서 매개변수에 파라미터 바인딩*/}
//    private final EntityManager em;
//
//    public void save(Item item) {
//        if (item.getId() == null) {
//            em.persist(item);
//        } else {
//            em.merge(item);
//        }
//    }
//    public Item findOne(Long id){
//        return em.find(Item.class,id);
//    }
//    public List<Item> findAll(){
//        return em.createQuery("select ??",Item.class)
//                .getResultList();
//    }
}