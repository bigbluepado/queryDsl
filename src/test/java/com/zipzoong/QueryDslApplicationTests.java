package com.zipzoong;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipzoong.querydsl.QueryDslApplication;
import com.zipzoong.querydsl.entity.Hello;
import com.zipzoong.querydsl.entity.QHello;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
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
