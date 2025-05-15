package com.example.rewear.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    KNIT("Knit"),
    SWEATSHIRT("Sweatshirt"),
    SHIRT("Shirt"),
    SHORTS("Shorts"),
    JEANS("Jeans"),
    SWEATPANTS("Sweatpants"),
    ZIP_UP_HOODIE("Zip-up Hoodie"),
    PUFFER_JACKET("Puffer Jacket"),
    JACKET("Jacket"),
    DRESS("Dress"),
    SKIRT("Skirt");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Category from(String key) {
        // label 기반 매핑
        for (Category c : values()) {
            if (c.label.equalsIgnoreCase(key)) {
                return c;
            }
        }
        // name 기반 fallback
        String normalized = key.trim().replaceAll("[ \\-]", "_").toUpperCase();
        for (Category c : values()) {
            if (c.name().equals(normalized)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + key);
    }
}
