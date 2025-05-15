package com.example.rewear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PartnerDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartnerResponseDto{
        private Long id;
        private String name;
        private String logoUrl;
    }
}
