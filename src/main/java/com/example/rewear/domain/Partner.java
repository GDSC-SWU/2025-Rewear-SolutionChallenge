package com.example.rewear.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "partner_options", joinColumns = @JoinColumn(name = "partner_id"))
    @Column(nullable = false)
    private List<String> reformLists = new ArrayList<>();

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL)
    private List<Reform> reformList = new ArrayList<>();


}
