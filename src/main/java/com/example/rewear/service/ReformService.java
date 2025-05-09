package com.example.rewear.service;

import com.example.rewear.domain.Item;
import com.example.rewear.domain.Partner;
import com.example.rewear.domain.Reform;
import com.example.rewear.domain.enums.ReformStatus;
import com.example.rewear.dto.ReformDto;
import com.example.rewear.repository.ItemRepository;
import com.example.rewear.repository.PartnerRepository;
import com.example.rewear.repository.ReformRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReformService {
    private final ReformRequestRepository reformRequestRepository;
    private final ItemRepository itemRepository;
    private final PartnerRepository partnerRepository;

    //View full remodel request
    @Transactional(readOnly = true)
    public List<ReformDto.ReformListDto> listAll(){
        return reformRequestRepository.findAll().stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    //View reform requests by status
    @Transactional(readOnly = true)
    public List<ReformDto.ReformListDto> listByStatus(ReformStatus reformStatus){
        return reformRequestRepository.findAllByReformStatus(reformStatus).stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    private ReformDto.ReformListDto toListDto(Reform reform){
        return ReformDto.ReformListDto.builder()
                .id(reform.getId())
                .itemId(reform.getItem().getId())
                .partnerId(reform.getPartner().getId())
                .partnerName(reform.getPartner().getName())
                .title(reform.getItem().getTitle())
                .category(reform.getItem().getCategory())
                .imageUrls(reform.getItem().getImageUrls())
                .reformStatus(reform.getReformStatus())
                .build();
    }

    public ReformDto.PartnerChooseResponseDto completeSelect(Long itemId, ReformDto.PartnerChooseRequestDto request){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()->new IllegalArgumentException("\n" +
                "Item not found: " + itemId));
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new IllegalArgumentException("Partner not found: " + request.getPartnerId()));

        Reform reform = Reform.builder()
                .item(item)
                .partner(partner)
                .chosenOption(request.getChosenOption())
                .previewMediaUrl(request.getPreviewImageUrl())
                .reformStatus(ReformStatus.PENDING)
                .build();

        Reform result = reformRequestRepository.save(reform);

        return ReformDto.PartnerChooseResponseDto.builder()
                .itemId(result.getItem().getId())
                .partnerId(result.getPartner().getId())
                .chosenOption(result.getChosenOption())
                .previewImageUrl(result.getPreviewMediaUrl())
                .build();

    }
}
