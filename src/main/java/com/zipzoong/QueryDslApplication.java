package com.zipzoong;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class QueryDslApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryDslApplication.class, args);
    }

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em){
        return new JPAQueryFactory(em);
    }

    @Bean
    public AuditorAware<String> auditorProvider(){
         //실제로는 Session에서 ID 를 꺼내서 넣어주면 된다.
         // 지금은 테스트이니 랜덤하게 넣어서 한다.
        return () -> Optional.of(UUID.randomUUID().toString());
    }

}
