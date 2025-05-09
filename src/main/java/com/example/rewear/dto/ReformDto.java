package com.example.rewear.dto;

import com.example.rewear.domain.enums.Category;
import com.example.rewear.domain.enums.ReformStatus;
import com.example.rewear.domain.enums.ReformType;
import lombok.*;
import java.util.List;

public class ReformDto {

    //Choose Reform or Donate
    @Getter
    @Builder
    public static class ReformOrNotRequestDto{
        private Long itemId;
        private ReformType reformType;
    }


    //Selections received after handing over data to Gemini
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiResponseDto{
        private List<String> options;
    }


    //Hand over one of the selections back to Gemini
    @Getter
    @Setter
    public static class SelectionRequestDto{
        private String chosenOption;
    }


    //an image that shows how you've reformed your body
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectionResponseDto{
        private String previewUrl;
    }


    //Request for selecting a partner
    @Getter
    @Builder
    public static class PartnerChooseRequestDto{
        private Long partnerId;
        // private Long itemId;
        private String chosenOption;
        private String previewImageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartnerChooseResponseDto{
        private Long itemId;
        private Long partnerId;
        private String chosenOption;
        private String previewImageUrl;
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
