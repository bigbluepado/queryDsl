package com.zipzoong;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipzoong.entity.Hello;
import com.zipzoong.entity.QHello;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest(classes = QueryDslApplication.class)
@Transactional
//@Commit
public class QueryDslApplicationTests {

    @PersistenceContext
    EntityManager em;


    @Test
    void contextLodes(){

        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = new QHello("h");

        Hello result = query.selectFrom(qHello).fetchOne();

        Assertions.assertThat(result).isEqualTo(hello);

        Assertions.assertThat(result.getId()).isEqualTo(hello.getId());


    }
}
