package com.example.rewear.dto;

import com.example.rewear.domain.enums.Category;
import com.example.rewear.domain.enums.ReformStatus;
import com.example.rewear.domain.enums.ReformType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReformDto {



    @Getter
    @Builder
    public static class ReformRequestDto{
        private Long itemId;
        private ReformType reformType;
        private String chosenOption;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    //Look up the list of reform requests
    public static class ReformListDto{
        private Long id;
        private Long itemId;
        private Long partnerId;
        private String partnerName;
        private String title;
        private Category category;
        private List<String> imageUrls;
        private ReformStatus reformStatus;
    }
}
