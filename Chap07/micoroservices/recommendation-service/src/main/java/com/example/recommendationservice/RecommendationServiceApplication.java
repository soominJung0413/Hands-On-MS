package com.example.recommendationservice;

import com.example.recommendationservice.persistence.RecommendationEntity;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication(scanBasePackages = "com.example")
@Slf4j
public class RecommendationServiceApplication {

    @Value("${spring.r2dbc.host}")
    private String r2dbcHost;

    @Bean
    public PostgresqlConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(r2dbcHost)
                        .username("postgres")
                        .password("tnals12@")
                        .database("simple")
                        .build()
        );
    }


    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(RecommendationServiceApplication.class, args);
        String r2dbcUrl = ctx.getEnvironment().getProperty("spring.r2dbc.url");
        log.info("Connected to PostgreSQL: " + r2dbcUrl);
    }

}
