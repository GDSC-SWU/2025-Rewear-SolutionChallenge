package com.example.rewear.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    TOPS_KNIT, TOPS_SWEATSHIRT, TOPS_SHIRT,
    BOTTOMS_SHORTS, BOTTOMS_JEANS, BOTTOMS_SWEATPANTS,
    OUTER_ZIP_UP_HOODIE, OUTER_PUFFER_JACKET, OUTER_JACKET,
    DRESS,
    SKIRT;

    @JsonCreator
    public static Category from(String key) {
        return Category.valueOf(
                key.trim()
                        .replaceAll("[ \\-]", "_")   // Space/hyphen → underscore
                        .toUpperCase()
        );
    }

}
