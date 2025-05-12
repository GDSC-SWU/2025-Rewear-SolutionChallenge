package com.example.rewear.controller;

import com.example.rewear.dto.GeminiDto;
import com.example.rewear.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/items/{itemId}/action")
@RequiredArgsConstructor
@RestController
public class GeminiController {
    private final GeminiService geminiService;


    @PostMapping
    public ResponseEntity<GeminiDto.GeminiResponseDto> chooseAction(
            @PathVariable Long itemId,
            @RequestBody GeminiDto.ReformOrNotRequestDto request
    ) {
        return ResponseEntity.ok(
                geminiService.requestReformOption(itemId, request.getReformType())
        );
    }

    @PostMapping("/reformSelection")
    public ResponseEntity<GeminiDto.SelectionResponseDto> selectionOption(
            @PathVariable Long itemId,
            @RequestBody GeminiDto.SelectionRequestDto selectRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                geminiService.aiRequest(itemId, selectRequest.getOption())
        );
    }
}