package com.example.rewear.controller;

import com.example.rewear.domain.enums.ReformStatus;
import com.example.rewear.dto.ReformDto;
import com.example.rewear.service.ReformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class ReformController {

    private final ReformService reformService;

    @GetMapping("/reform/items")
    public ResponseEntity<List<ReformDto.ReformListDto>> listByStatus(
            @RequestParam(value = "status", required = false) ReformStatus reformStatus){

        List<ReformDto.ReformListDto> reformList;

        if (reformStatus == null) {
            // ALL without status parameter
            reformList = reformService.listAll();
        } else {
            switch (reformStatus) {
                case IN_PROGRESS:
                    reformList = reformService.listByStatus(ReformStatus.IN_PROGRESS);
                    break;
                case COMPLETED:
                    reformList = reformService.listByStatus(ReformStatus.COMPLETED);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown status: " + reformStatus);
            }
        }

        return ResponseEntity.ok(reformList);
    }



}
