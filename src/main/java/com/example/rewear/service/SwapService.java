package com.example.rewear.service;

import com.example.rewear.domain.Item;
import com.example.rewear.domain.Swap;
import com.example.rewear.dto.SwapDto;
import com.example.rewear.repository.ItemRepository;
import com.example.rewear.repository.SwapRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SwapService {
    private final SwapRequestRepository swapRequestRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public SwapDto.SwapResponseDto save(Long itemId, SwapDto.SwapRequestDto request){

        Item item = itemRepository.findById(itemId)
                .orElseThrow(()->new IllegalArgumentException("\n" +
                        "Item not found: " + itemId));

        Swap swap = Swap.builder()
                .item(item)
                .swapStatus(request.getSwapStatus())
                .build();

        swapRequestRepository.save(swap);

        return SwapDto.SwapResponseDto.builder()
                .id(swap.getId())
                .itemId(swap.getItem().getId())
                .swapStatus(swap.getSwapStatus())
                .createdAt(swap.getCreatedAt())
                .build();
    }

}
