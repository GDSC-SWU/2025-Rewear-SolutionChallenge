package com.example.rewear.dto;

import com.example.rewear.domain.enums.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

public class ItemDto {

    @Getter
    @Builder
    public static class ItemRequestDto{

        @NotBlank(message = "Please write a title")
        private String title;
        private Category category;
        private List<String> imageUrls;
        private String description;
        private String swapMethod;
    }

    //Item registration response
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemResponseDto{
        private Long id;
        private String title;
        private Category category;
        private List<String> imageUrls;
        private String description;
        private String swapMethod;
    }

    //Response to query the home screen item list
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemListDto{
        private Long id;
        private String title;
        private Category category;
        private String address;
        private int likeCount;
        private List<String> imageUrls;
    }


}
