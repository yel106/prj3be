package com.example.prj3be.service;

import com.example.prj3be.domain.Item;
import com.example.prj3be.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;

//상품 등록, 상품 목록 조회 ., 상품 수정 , 삭제
@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
//   아이템 등록
    public void add(Item item){
        itemRepository.save(item);
    }
//    이미 존재하는 상품 정보 확인
    public void handleUpdateItem(Item updateItem){
        Item existingItem = itemRepository.findItemsById(updateItem.getId()).orElse(null);

        if (existingItem != null){
            existingItem.setId(updateItem.getId());
            existingItem.setTitle(updateItem.getTitle());
            existingItem.setArtist(updateItem.getArtist());
            existingItem.setReleaseDate(updateItem.getReleaseDate());
            existingItem.setAlbumFormat(updateItem.getAlbumFormat());
            existingItem.setAgency(updateItem.getAgency());
            existingItem.setPrice(updateItem.getPrice());
            itemRepository.save(existingItem);
        }
        else {
            itemRepository.save(updateItem);
        }
    }

    //id로 특정 item 상품 조회
    public Item handleFindById(Long id){
        return itemRepository.findItemsById(id).orElse(null);
    }

    public List<Item> findAllItem(){
        return itemRepository.findAll();
    }
}