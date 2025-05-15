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
    public ResponseEntity<List<ItemDto.ItemListDto>> findAllItems(
            @RequestParam(value = "address", required = false) String regionName) {

        if (regionName == null || regionName.isEmpty()) {
            regionName = regionService.getSavedAddress(); // 세션에서 가져옴
        }

        List<ItemDto.ItemListDto> items = itemService.findItems(regionName);
        return ResponseEntity.ok(items);
    }

    //Item registration
    @PostMapping("/items")
    public ResponseEntity<ItemDto.ItemResponseDto> addItem(
            @RequestParam(value = "address", required = false) String address, @RequestBody ItemDto.ItemRequestDto request){
        if (address != null && !address.isEmpty()) {
            regionService.saveAddress(address);
        } else {
            address = regionService.getSavedAddress();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.save(request, address));
    }

    //Detailed search for a specific item
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ItemDto.ItemResponseDto> findItem(@PathVariable long itemId){
        ItemDto.ItemResponseDto itemResponse = itemService.findItem(itemId);
        return ResponseEntity.ok().body(itemResponse);
    }

}
