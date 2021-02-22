package com.example.api.composite.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 외부의 요청을 받아 핵심 MS 에 비동기적 호출을 가하는 인터페이스
 * Swaager 사용
 */
@Api(description = "Rest API for Composite product Information.")
public interface ProductCompositeService {

    /**
     *  각 Swagger 문서 설명은 해당 인터페이스 구현체의 프로젝트 설정파일에서 작성해야함
     */


    /**
     * curl -X POST $HOST:$PORT/product-composite \
     *      -H "ContentType: application/json" --data \
     *      '{"productId":123, "name":"product 123", "weight":123}'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.product-composite.create-composite-product.description}",
            notes = "${api.product-composite.create-composite-product.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, 올바른 형식의 요청이 아님. 자세한 내용은 응답에 기재"),
            @ApiResponse(code = 422, message = "UnProcessable Entity, 인풋 파라미터에 의해 프로세스 실패됨, 자세한 내용은 응답에 기재")
    })
    @PostMapping(
            value = "/product-composite",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void createCompositeProduct(@RequestBody ProductAggregate body);


    /**
     * curl -X GET $HOST:$PORT/product-composite/1
     *
     * Mono 로 리턴 값을 가지는 것이 핵심.
     *
     * @param productId
     * @return
     */
    @ApiOperation(
            value = "${api.product-composite.get-composite-product.description}",
            notes = "${api.product-composite.get-composite-product.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, 올바른 형식의 요청이 아님. 자세한 내용은 응답에 기재"),
            @ApiResponse(code = 422, message = "UnProcessable Entity, 인풋 파라미터에 의해 프로세스 실패됨, 자세한 내용은 응답에 기재"),
            @ApiResponse(code = 404, message = "Not Found, Id 에 대한 값이 존재하지 않음")
    })
    @GetMapping(
            value = "/product-composite/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Mono<ProductAggregate> getCompositeProduct(@PathVariable int productId);


    /**
     * curl -X DELETE $HOST:$PORT/product-composite/1
     *
     * @param productId
     */
    @ApiOperation(
            value = "${api.product-composite.delete-composite-product.description}",
            notes = "${api.product-composite.delete-composite-product.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, 올바른 형식의 요청이 아님. 자세한 내용은 응답에 기재"),
            @ApiResponse(code = 422, message = "UnProcessable Entity, 인풋 파라미터에 의해 프로세스 실패됨, 자세한 내용은 응답에 기재")
    })
    @DeleteMapping(value = "/product-composite/{productId}")
    void deleteCompositeProduct(@PathVariable int productId);
}
