package com.example.reviewservice.services;

import com.example.api.core.review.Review;
import com.example.reviewservice.persistence.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * Mapper 를 Spring Bean 으로 구현하기 위한 인터페이스
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {

    /**
     *  @Mapping 의 Target 속성은 바꿀 모델이 가지고 있는 필드로 매개변수에 없는 값으로 인한
     *  맵핑 에러를 회피하기위해 쓰인다.
     *
     * @param entity
     * @return
     */
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Review entityToApi(ReviewEntity entity);


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ReviewEntity apiToEntity(Review api);

    /**
     *  기본 맵핑이 올바르게 잡혀있다면  List 단의 맵핑은 자동으로 처리해준다.
     */
    List<Review> entityListToApiList(List<ReviewEntity> entity);
    List<ReviewEntity> apiListToEntityList(List<Review> api);
}
