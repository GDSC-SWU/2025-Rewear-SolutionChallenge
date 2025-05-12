package com.example.rewear.dto;

import com.example.rewear.domain.enums.ReformType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

public class GeminiDto {

    //Choose Reform or Donate
    @Getter
    @Builder
    public static class ReformOrNotRequestDto{
        //private Long itemId;
        private ReformType reformType;
    }


    //Selections received after handing over data to Gemini
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiResponseDto{
        @JsonProperty("recommendations")
        private List<String> recommendations;
    }


    //Hand over one of the selections back to Gemini
    @Getter
    @Setter
    public static class SelectionRequestDto{
        @JsonProperty("option")
        private String option;
    }


    //an image that shows how you've reformed your body
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectionResponseDto{
        @JsonProperty("image_base64")
        private String image_base64;
    }

}
