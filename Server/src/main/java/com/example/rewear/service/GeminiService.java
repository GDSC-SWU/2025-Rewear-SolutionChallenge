package com.example.rewear.service;

import com.example.rewear.domain.Item;
import com.example.rewear.domain.Reform;
import com.example.rewear.domain.enums.ReformStatus;
import com.example.rewear.domain.enums.ReformType;
import com.example.rewear.dto.GeminiDto;
import com.example.rewear.repository.ItemRepository;
import com.example.rewear.repository.ReformRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class GeminiService {
    private final RestTemplate restTemplate;
    private final ItemRepository itemRepository;
    private final ReformRequestRepository reformRequestRepository;

    @Value("${ai.api.base-url}")
    private String aiBaseUrl;

    public GeminiDto.GeminiResponseDto requestReformOption(
            Long itemId, ReformType reformType
    ) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        Reform reform = Reform.builder()
                .item(item)
                .reformType(reformType)
                .reformStatus(ReformStatus.PENDING)
                .chosenOption("")
                .previewMediaUrl("")
                .build();
        reformRequestRepository.save(reform);

        // RestTemplate 동기 호출
        return restTemplate.postForObject(
                aiBaseUrl + "/recommend",
                Map.of("clothing_info", item.getTitle()),
                GeminiDto.GeminiResponseDto.class
        );
    }

    public GeminiDto.SelectionResponseDto aiRequest(
            Long itemId, String option
    ) {
        Reform reform = reformRequestRepository
                .findTopByItemIdAndReformStatusOrderByIdDesc(itemId, ReformStatus.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Pending reform not found"));

        reform.setChosenOption(option);
        reformRequestRepository.save(reform);

        GeminiDto.SelectionResponseDto resp = restTemplate.postForObject(
                aiBaseUrl + "/generate-preview",
                Map.of("option", option),
                GeminiDto.SelectionResponseDto.class
        );
        if (resp != null) {
            reform.setPreviewMediaUrl(resp.getImage_base64());
            reformRequestRepository.save(reform);
        }
        return resp;
    }
}
