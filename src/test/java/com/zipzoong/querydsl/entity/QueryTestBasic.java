package com.zipzoong.querydsl.entity;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static com.zipzoong.querydsl.entity.QMember.*;
import static com.zipzoong.querydsl.entity.QTeam.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
//@Commit
class QueryTestBasic {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void fatchJoinOn(){

        List<Tuple> results = queryFactory.select(member, team)
                .from(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetch();

        for(Tuple tuple : results){
            System.out.println("tuple =" + tuple);
        }
    }

    /*
    나이가 최고 많은 회원 조회
     */
    @Test
    public void subQeuery(){
        QMember qMemberSub = new QMember("mSub");
        List<Member> result = queryFactory.select(member)
                .from(member)
                .where(
                        member.age.eq(
                                select(qMemberSub.age.max())
                                        .from(qMemberSub)
                        )
                ).fetch();

        for(Member member : result){
            System.out.println("member =" + member);
        }

        assertThat(result.get(0).getAge()).isEqualTo(40);

        assertThat(result).extracting("age").containsExactly(40);
    }

    /*
    나이가 평균 이상인 회원 조회
     */
    @Test
    public void subQeruryGoe(){
        QMember qMemberSub = new QMember("mSub");

        List<Member> results = queryFactory.select(member)
                .from(member)
                .where(
                        member.age.goe(
                                select(qMemberSub.age.avg())
                                        .from(qMemberSub)
                        )
                ).fetch();

        for (Member member : results){
            System.out.println("member :"+ member);
        }

        assertThat(results).extracting("age").containsExactly(30,40);
    }


    /*
       나이가 10살 이상인 회원 조회
     */
    @Test
    public void subQeruryGoeIn(){
        QMember qMemberSub = new QMember("mSub");

        List<Member> results = queryFactory.select(member)
                .from(member)
                .where(
                        member.age.in(
                                select(qMemberSub.age)
                                        .from(qMemberSub)
                                        .where(qMemberSub.age.gt(10))
                        )
                ).fetch();

        for (Member member : results){
            System.out.println("member :"+ member);
        }

        assertThat(results).extracting("age").containsExactly(20,30,40);
    }


    /*
       나이가 10살 이상인
     */
    @Test
    public void selectSubQerury(){
        QMember memberSub = new QMember("mSub");

        List<Tuple> fetch = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub)
                ).from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("username = " + tuple.get(member.username));
            System.out.println("age = " +
                    tuple.get(select(memberSub.age.avg())
                            .from(memberSub)));
        }
    }

    @Test
    public void constant(){
        List<Tuple> results = queryFactory.select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple result : results) {
            System.out.println("result = " + result);
        }
    }

    @Test
    public void concat(){
        List<String> result = queryFactory.select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);;
        }
    }


}