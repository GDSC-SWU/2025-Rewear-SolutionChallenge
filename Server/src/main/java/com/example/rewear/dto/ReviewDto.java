package com.example.rewear.dto;

import com.example.rewear.domain.enums.ReviewTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewDto {

    @Getter
    @Builder
    public static class ReviewRequestDto{
        private Integer rating;
        private List<ReviewTag> reviewTagList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewResponseDto{
        private Long id;
        private Long swapRequestId;
        private Long userId;
        private String reviewNickname;
        private Integer rating;
        private List<ReviewTag> reviewTagList;
        private LocalDateTime createdAt;
    }

}
