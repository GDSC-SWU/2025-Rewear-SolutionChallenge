package com.example.rewear.controller;

import com.example.rewear.dto.RegionDto;
import com.example.rewear.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class RegionController {
    private final RegionService regionService;

    @PostMapping("/location")
    public ResponseEntity<RegionDto> addLocation(@RequestBody RegionDto region){
        regionService.saveAddress(region.getAddress());

        return ResponseEntity.ok(region);

    }
}
