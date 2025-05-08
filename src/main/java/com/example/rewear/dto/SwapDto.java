package com.example.rewear.dto;

import com.example.rewear.domain.enums.SwapStatus;
import lombok.*;

import java.time.LocalDateTime;

public class SwapDto {
    @Getter
    @Setter
    public static class SwapRequestDto{
        //private Long userId;
        private SwapStatus swapStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SwapResponseDto{
        private Long id;
        private Long itemId;
        //private Long userId;
        private SwapStatus swapStatus;
        private LocalDateTime createdAt;
    }
}
