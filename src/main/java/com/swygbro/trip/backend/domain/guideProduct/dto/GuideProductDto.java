package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategory;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.review.dto.DetailReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideProductDto {
    @Schema(description = "유저 ID", example = "1")
    private Long userId;
    @Schema(description = "사용자 닉네임", example = "nickname01")
    private String nickname;
    @Schema(description = "상품 ID", example = "1")
    private Long id;
    @Schema(description = "상품 제목", example = "신나는 서울 투어")
    private String title;
    @Schema(description = "상품 설명", example = "서울 *** 여행 가이드 합니다.")
    private String description;
    @Schema(description = "가이드 비용", example = "10000")
    private Long price;
    @Schema(description = "가이드 위치 이름", example = "한강 공원")
    private String locationName;
    @Schema(description = "가이드 위치(위도)", example = "37")
    private double latitude;
    @Schema(description = "가이드 위치(경도)", example = "127")
    private double longitude;
    @Schema(description = "가이드 시작 날짜", example = "2024-05-01", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate guideStart;
    @Schema(description = "가이드 종료 날짜", example = "2024-05-01", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate guideEnd;
    @Schema(description = "가이드 시작 시간", example = "12:00:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    private LocalTime guideStartTime;
    @Schema(description = "가이드 종료 시간", example = "20:00:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    private LocalTime guideEndTime;
    @Schema(description = "가이드 소요 시간", example = "3")
    private int guideTime;
    @Schema(description = "상품 카테고리", example = "[\"DINING\", \"OUTDOOR\"]")
    private List<GuideCategoryCode> categories;
    @Schema(description = "대표 이미지 url", example = "https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입")
    private String thumb;
    @Schema(description = "상품 이미지 url", example = "[\"https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입\", \"...\"]")
    private List<String> images;
    @Schema(description = "상품 리뷰")
    private List<DetailReviewDto> reviews;

    public static GuideProductDto fromEntity(GuideProduct product) {
        List<GuideCategoryCode> categories = product.getCategories().stream().map(GuideCategory::getCategoryCode).toList();

        return GuideProductDto.builder().id(product.getId())
                .userId(product.getUser().getId())
                .nickname(product.getUser().getNickname())
                .title(product.getTitle())
                .description(product.getDescription()).price(product.getPrice()).locationName(product.getLocationName())
                .longitude(product.getLocation().getX()).latitude(product.getLocation().getY())
                .guideStart(product.getGuideStart().toLocalDate()).guideEnd(product.getGuideEnd().toLocalDate())
                .guideStartTime(product.getGuideStartTime()).guideEndTime(product.getGuideEndTime())
                .guideTime(product.getGuideTime()).categories(categories)
                .thumb(product.getThumb()).images(product.getImages())
                .reviews(product.getReviews().stream().map(DetailReviewDto::fromEntity).collect(Collectors.toList()))
                .build();
    }
}
