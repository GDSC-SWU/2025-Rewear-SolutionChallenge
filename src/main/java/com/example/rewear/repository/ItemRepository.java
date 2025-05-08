package com.example.rewear.repository;

import com.example.rewear.domain.Item;
import com.example.rewear.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Override
    Optional<Item> findById(Long itemId);
    List<Item> findAllByRegion_Address(String address);


}
