package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.swygbro.trip.backend.domain.guideProduct.dto.SearchCategoriesRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.SearchGuideProductResponse;
import com.swygbro.trip.backend.domain.user.domain.Language;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface GuideProductCustomRepository {

    Optional<GuideProduct> findDetailById(Long productId);

    List<SearchGuideProductResponse> findByLocation(Geometry geometry, int radius);

    Page<SearchGuideProductResponse> findByFilter(MultiPolygon region, ZonedDateTime start, ZonedDateTime end,
                                                  SearchCategoriesRequest categories, Long minPrice, Long maxPrice,
                                                  int minDuration, int maxDuration, DayTime dayTime,
                                                  Nationality nationality, List<Language> languages, Pageable pageable);

    List<SearchGuideProductResponse> findByBest(MultiPolygon polygon);

    Page<SearchGuideProductResponse> findAllWithMain(Pageable pageable);
}
