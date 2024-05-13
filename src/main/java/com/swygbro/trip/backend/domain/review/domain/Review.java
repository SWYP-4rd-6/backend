package com.swygbro.trip.backend.domain.review.domain;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.review.dto.CreateReviewRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User reviewer;

    @ManyToOne
    private GuideProduct guideProduct;

    @Column
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @OneToMany(mappedBy = "review", cascade = {CascadeType.PERSIST})
    private List<ReviewImage> images = new ArrayList<>();

    public Review(CreateReviewRequest request, User reviewer, GuideProduct guideProduct) {
        this.reviewer = reviewer;
        this.guideProduct = guideProduct;
        this.content = request.getContent();
        this.rating = request.getRating();
    }

    public void addReviewImage(ReviewImage image) {
        this.images.add(image);
    }

}
