package com.example.rewear.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region{
    //@Column(nullable = false)
    //private Double latitude;

    //@Column(nullable = false)
    //private Double longitude;

    private String address;

    public Region(String address){
        //this.latitude = latitude;
        //this.longitude = longitude;
        this.address = address;
    }

}
