package com.example.rewear.controller;

import com.example.rewear.dto.GeminiDto;
import com.example.rewear.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/api/items/{itemId}/action")
@RequiredArgsConstructor
@RestController
public class GeminiController {
    private final GeminiService geminiService;

    @PostMapping
    public Mono<ResponseEntity<GeminiDto.GeminiResponseDto>> chooseAction(
            @PathVariable Long itemId, @RequestBody GeminiDto.ReformOrNotRequestDto request) {

        //Internally in the service layer, AI request is made using itemId and request.getReformType()
        return geminiService.requestReformOption(itemId, request.getReformType())
                .map(resp -> ResponseEntity.ok(resp));
    }

    @PostMapping("/reformSelection")
    public Mono<ResponseEntity<List<GeminiDto.SelectionResponseDto>>> selectionOption(
            @PathVariable Long itemId, @RequestBody GeminiDto.SelectionRequestDto selectRequest){

        return geminiService.aiRequest(itemId, selectRequest.getChosenOption())
                .map(aiList -> ResponseEntity.status(HttpStatus.CREATED).body(aiList));
    }

}
