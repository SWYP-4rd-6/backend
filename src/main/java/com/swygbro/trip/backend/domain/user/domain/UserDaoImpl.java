package com.swygbro.trip.backend.domain.user.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.guideProduct.domain.QGuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.dto.SimpleGuideProductDto;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;


@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UserProfileDto> getUserProfile(Long userId) {

        QUser qUser = QUser.user;
        QGuideProduct qGuideProduct = QGuideProduct.guideProduct;

        var res = queryFactory
                .from(qUser)
                .leftJoin(qGuideProduct).on(qGuideProduct.user.eq(qUser))
                .where(qUser.id.eq(userId))
                .orderBy(qGuideProduct.guideStart.desc())
                .limit(4)
                .transform(
                        groupBy(qUser.id)
                                .list(Projections.fields(
                                                UserProfileDto.class,
                                                qUser.email,
                                                qUser.nickname,
                                                qUser.name,
                                                qUser.profile,
                                                qUser.profileImageUrl,
                                                list(Projections.fields(
                                                        SimpleGuideProductDto.class,
                                                        qGuideProduct.id,
                                                        qGuideProduct.title,
                                                        qGuideProduct.description,
                                                        qGuideProduct.thumb,
                                                        qGuideProduct.guideStart,
                                                        qGuideProduct.guideEnd
                                                ).skipNulls()).as("guideProducts")
                                        )
                                )
                );

        return res.stream().findFirst();
    }
}