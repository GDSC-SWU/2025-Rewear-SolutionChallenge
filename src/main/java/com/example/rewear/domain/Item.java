package com.example.rewear.domain;

import com.example.rewear.domain.enums.Category;
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
public class Item extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Embedded
    //Region이 값타입이기 때문에 설정 필요 -> 이렇게하면, item 테이블에 latitude, longitude, address 컬럼이 바로 생성
    private Region region;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "url", nullable = false)
    private List<String> imageUrls = new ArrayList<>();

    @Column(columnDefinition = "Text")
    private String description;

    private String swapMethod;

    public void addImage(String url){
        this.imageUrls.add(url);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<UserLike> userLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Swap> swapList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Reform> reformList = new ArrayList<>();
}
