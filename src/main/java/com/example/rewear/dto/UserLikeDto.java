package com.example.rewear.dto;

import lombok.*;

public class UserLikeDto {

    @Getter
    @Setter
    public static class UserLikeRequestDto{
        private Integer likeCounts;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLikeResponseDto{
        private Long id;
        private Long itemId;
        private Integer likeCounts;

    }

}
