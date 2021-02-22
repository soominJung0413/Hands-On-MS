package com.example.util.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 리엑티브 객체를 이해하기위한 간단한 테스트
 */
public class ReactorTests {

    @Test
    public void TestFlux() {
        List<Integer> list = new ArrayList<>();
        Flux.just(1, 2, 3, 4)
                .filter(n -> n % 2 == 0)
                .map(n -> n * 2)
                .log()
                .subscribe(n -> list.add(n));

        assertThat(list).containsExactly(4,8);
    }

    @Test
    public void TestFluxBlocking() {
        List<Integer> list = Flux.just(1, 2, 3, 4)
                .filter(n -> n % 2 == 0)
                .map(n -> n * 2)
                .log()
                .collectList().block();

        assertThat(list).containsExactly(4, 8);
    }
}
