package com.example.rewear.controller;

import com.example.rewear.dto.PartnerDto;
import com.example.rewear.service.ItemService;
import com.example.rewear.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/items/{itemId}/reformSelection/partner")
@RequiredArgsConstructor
@RestController
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping
    public ResponseEntity<List<PartnerDto.PartnerResponseDto>> partnerList(@PathVariable Long itemId){
        List<PartnerDto.PartnerResponseDto> partners= partnerService.findPartner(itemId);
        return ResponseEntity.ok().body(partners);
    }

}
