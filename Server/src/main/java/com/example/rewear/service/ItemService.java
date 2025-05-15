package com.example.rewear.service;

import com.example.rewear.domain.Item;
import com.example.rewear.domain.Region;
import com.example.rewear.dto.ItemDto;
import com.example.rewear.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    @Transactional(readOnly = true)
    public List<ItemDto.ItemListDto> findItems(String address) {
        List<Item> itemList = itemRepository.findAllByRegion_Address(address);

        return itemList.stream()
                .map(item -> ItemDto.ItemListDto.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .category(item.getCategory())
                        .imageUrls(item.getImageUrls())
                        .address(item.getRegion().getAddress())
                        .likeCount(item.getUserLikeList().size())
                        .build())
                .collect(Collectors.toList());
    }

    public ItemDto.ItemResponseDto save(ItemDto.ItemRequestDto request, String address) {
        Region region = new Region(address);

        Item item = Item.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .description(request.getDescription())
                .swapMethod(request.getSwapMethod())
                .imageUrls(request.getImageUrls())
                .region(region)
                .build();

        itemRepository.save(item);

        return ItemDto.ItemResponseDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .category(item.getCategory())
                .description(item.getDescription())
                .swapMethod(item.getSwapMethod())
                .imageUrls(item.getImageUrls())
                .build();
    }

    @Transactional(readOnly = true)
    public ItemDto.ItemResponseDto findItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("\n" +
                        "The product you are looking for could not be found."));

        return ItemDto.ItemResponseDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .category(item.getCategory())
                .description(item.getDescription())
                .swapMethod(item.getSwapMethod())
                .imageUrls(item.getImageUrls())
                .build();
    }
}
