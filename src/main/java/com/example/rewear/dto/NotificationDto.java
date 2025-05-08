package com.example.rewear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NotificationDto {

    @Getter
    @Builder
    public static class NotificationRequestDto{

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationResponseDto{
        private Long id;
        private String title;
        private String message;
        private Boolean readCheck;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
