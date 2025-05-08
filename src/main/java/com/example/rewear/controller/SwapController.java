package com.example.rewear.controller;

import com.example.rewear.dto.SwapDto;
import com.example.rewear.service.SwapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class SwapController {
    private final SwapService swapService;

    //Screen after pressing the swap button
    @PutMapping("/items/{itemId}/swap")
    public ResponseEntity<SwapDto.SwapResponseDto> requestSwap(@PathVariable Long itemId, @RequestBody SwapDto.SwapRequestDto request) {
        SwapDto.SwapResponseDto swapResponse = swapService.save(itemId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(swapResponse);
    }
}
