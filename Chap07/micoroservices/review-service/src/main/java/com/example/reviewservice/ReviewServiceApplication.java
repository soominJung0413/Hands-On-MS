package com.example.reviewservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication(scanBasePackages = "com.example")
@Slf4j
public class ReviewServiceApplication {

    /**
     * 커넥션 풀 만큼  ThreadPoll 을 생성하여 메인스레드가 블로킹 되지 않게끔
     * Database 커넥션 로직을 ThreadPoll 에 태울 계획
     */
    private final Integer connectionPoolSize;

    @Autowired
    public ReviewServiceApplication(
            @Value("${spring.datasource.maximum-pool-size:10}")
            Integer connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    @Bean
    public Scheduler jdbcScheduler() {
        log.info("커넥션 풀 사이즈에 맞추어 스케쥴러를 생성합니다. 커넥션 풀 사이즈는 {} 입니다",connectionPoolSize);
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(ReviewServiceApplication.class, args);
        /**
         * Spring-Core 의 Environment 를 이용해 프로퍼티 값을 도출 -> Mysql 에 사용되는 URL
         */
        String psqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
        log.info("Coonected to PostgreSQL : {}",psqlUri);
    }
}
