package com.example.rewear.controller;

import com.example.rewear.dto.ItemDto;
import com.example.rewear.service.ItemService;

import com.example.rewear.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class ItemController {
    private final ItemService itemService;
    private final RegionService regionService;

    //View the home screen item list
    @GetMapping("/home")
    public ResponseEntity<List<ItemDto.ItemListDto>> findAllItems() {
        String regionName = regionService.getSavedAddress();
        List<ItemDto.ItemListDto> items = itemService.findItems(regionName);
        return ResponseEntity.ok(items);
    }

    //Item registration
    @PostMapping("/items")
    public ResponseEntity<ItemDto.ItemResponseDto> addItem(@Valid @RequestBody ItemDto.ItemRequestDto request){
        String regionName = regionService.getSavedAddress();
        ItemDto.ItemResponseDto itemRequest = itemService.save(request,regionName);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemRequest);
    }

    //Detailed search for a specific item
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ItemDto.ItemResponseDto> findItem(@PathVariable long itemId){
        ItemDto.ItemResponseDto itemResponse = itemService.findItem(itemId);
        return ResponseEntity.ok().body(itemResponse);
    }

}
