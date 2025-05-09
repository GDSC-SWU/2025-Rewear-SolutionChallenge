package com.example.rewear.service;

import com.example.rewear.domain.Item;
import com.example.rewear.domain.Partner;
import com.example.rewear.dto.PartnerDto;
import com.example.rewear.repository.ItemRepository;
import com.example.rewear.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final ItemRepository itemRepository;

    public List<PartnerDto.PartnerResponseDto> findPartner(Long itemId){
        itemRepository.findById(itemId)
                .orElseThrow(()->new IllegalArgumentException("\n" +
                        "Item not found: " + itemId));


        List<Partner> partnerList = partnerRepository.findAll();

        return partnerList.stream()
                .map(partner -> PartnerDto.PartnerResponseDto.builder()
                        .id(partner.getId())
                        .name(partner.getName())
                        .logoUrl(partner.getLogoUrl())
                        .build())
                .collect(Collectors.toList());
    }

}
