package com.example.rewear.service;

import com.example.rewear.domain.Item;
import com.example.rewear.domain.Reform;
import com.example.rewear.domain.enums.ReformStatus;
import com.example.rewear.domain.enums.ReformType;
import com.example.rewear.dto.GeminiDto;
import com.example.rewear.repository.ItemRepository;
import com.example.rewear.repository.ReformRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final WebClient webClient;
    private final ItemRepository itemRepository;
    private final ReformRequestRepository reformRequestRepository;

    public Mono<GeminiDto.GeminiResponseDto> requestReformOption(Long itemId, ReformType reformType) {
        return Mono.fromCallable(() ->
                        itemRepository.findById(itemId)
                                .orElseThrow(() -> new IllegalArgumentException("Item not found"))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(item -> {
                    Reform reform = Reform.builder()
                            .item(item)
                            .reformType(reformType)
                            .reformStatus(ReformStatus.PENDING)
                            .chosenOption("")
                            .previewMediaUrl("")
                            .build();
                    return Mono.fromCallable(() -> reformRequestRepository.save(reform))
                            .subscribeOn(Schedulers.boundedElastic())
                            .thenReturn(item);
                })
                .flatMap(item ->
                        webClient.post()
                                .uri("/recommend")
                                .bodyValue(Map.of("clothing_info", item.getTitle()))
                                .retrieve()
                                .bodyToMono(GeminiDto.GeminiResponseDto.class));
    }

    public Mono<List<GeminiDto.SelectionResponseDto>> aiRequest(Long itemId, String chosenOption) {
        return Mono.fromCallable(() ->
                        reformRequestRepository
                                .findTopByItemIdAndReformStatusOrderByIdDesc(itemId, ReformStatus.PENDING)
                                .orElseThrow(() -> new IllegalArgumentException("Pending reform not found"))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(reform ->
                        webClient.post()
                                .uri("/generate-Preview")
                                .bodyValue(Map.of("chosen_option", chosenOption))
                                .retrieve()
                                .bodyToFlux(GeminiDto.SelectionResponseDto.class)
                                .collectList()
                                .flatMap(aiList -> {
                                    if (!aiList.isEmpty()) {
                                        reform.setPreviewMediaUrl(aiList.get(0).getPreviewUrl());
                                        reformRequestRepository.save(reform);
                                    }
                                    return Mono.just(aiList);
                                })
                );
    }

}