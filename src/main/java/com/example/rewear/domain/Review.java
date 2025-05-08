package com.example.rewear.domain;

import com.example.rewear.domain.enums.ReviewTag;
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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    @CollectionTable(name = "review_tag", joinColumns = @JoinColumn(name = "review_id"))
    private List<ReviewTag> reviewTagList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swap_id")
    private Swap swap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;
}
