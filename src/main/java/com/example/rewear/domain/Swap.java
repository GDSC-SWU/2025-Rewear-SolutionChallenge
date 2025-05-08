package com.example.rewear.domain;

import com.example.rewear.domain.enums.SwapStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Swap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Integer swapCounts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'INITIAL'")
    private SwapStatus swapStatus = SwapStatus.INITIAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToMany(mappedBy = "swap", cascade = CascadeType.ALL)
    private List<Review> reviewList = new ArrayList<>();

    public void waite(){
        if(swapStatus != SwapStatus.INITIAL){
            throw new IllegalStateException("The request has already been processed.");
        }
        this.swapStatus = SwapStatus.PENDING;
    }
    public void confirm(){
        if (swapStatus != SwapStatus.PENDING) {
            throw new IllegalStateException("The request has already been accepted.");
        }
        this.swapStatus = SwapStatus.CONFIRMED;
    }

    public void reject(){
        if (swapStatus != SwapStatus.PENDING){
            throw new IllegalStateException("The request has already been rejected.");
        }
        this.swapStatus = SwapStatus.REJECTED;
    }

}
