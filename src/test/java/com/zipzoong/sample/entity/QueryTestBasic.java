package com.zipzoong.sample.entity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipzoong.sample.dto.*;
import com.zipzoong.sample.entity.Member;
import com.zipzoong.sample.entity.QMember;
import com.zipzoong.sample.entity.QTeam;
import com.zipzoong.sample.entity.Team;
import com.zipzoong.sample.repository.MemberJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static com.zipzoong.sample.entity.QMember.*;
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
    public void fatchJoinOn() {

        List<Tuple> results = queryFactory.select(member, QTeam.team)
                .from(member)
                .join(member.team, QTeam.team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetch();

        for (Tuple tuple : results) {
            System.out.println("tuple =" + tuple);
        }
    }

    /*
    나이가 최고 많은 회원 조회
     */
    @Test
    public void subQeuery() {
        QMember qMemberSub = new QMember("mSub");
        List<Member> result = queryFactory.select(member)
                .from(member)
                .where(
                        member.age.eq(
                                select(qMemberSub.age.max())
                                        .from(qMemberSub)
                        )
                ).fetch();

        for (Member member : result) {
            System.out.println("member =" + member);
        }

        assertThat(result.get(0).getAge()).isEqualTo(40);

        assertThat(result).extracting("age").containsExactly(40);
    }

    /*
    나이가 평균 이상인 회원 조회
     */
    @Test
    public void subQeruryGoe() {
        QMember qMemberSub = new QMember("mSub");

        List<Member> results = queryFactory.select(member)
                .from(member)
                .where(
                        member.age.goe(
                                select(qMemberSub.age.avg())
                                        .from(qMemberSub)
                        )
                ).fetch();

        for (Member member : results) {
            System.out.println("member :" + member);
        }

        assertThat(results).extracting("age").containsExactly(30, 40);
    }


    /*
       나이가 10살 이상인 회원 조회
     */
    @Test
    public void subQeruryGoeIn() {
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

        for (Member member : results) {
            System.out.println("member :" + member);
        }

        assertThat(results).extracting("age").containsExactly(20, 30, 40);
    }


    /*
       나이가 10살 이상인
     */
    @Test
    public void selectSubQerury() {
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
    public void constant() {
        List<Tuple> results = queryFactory.select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple result : results) {
            System.out.println("result = " + result);
        }
    }

    @Test
    public void concat() {
        List<String> result = queryFactory.select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
            ;
        }
    }


    @Test
    public void findMemberDtoBySetter() {
        List<MemberDto> results = queryFactory
                .select(Projections.bean(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto result : results) {

            System.out.println("result = " + result);
        }
    }

    @Test
    public void findMemberDtoByField() {
        List<MemberDto> results = queryFactory
                .select(Projections.fields(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto result : results) {

            System.out.println("result = " + result);
        }
    }




    @Test
    public void findMemberDtoByFieldIfChangFieldName() {
        QMember subMember = new QMember("subMember");
        List<UserDto> results = queryFactory
                .select(Projections.fields(
                        UserDto.class, member.username.as("name"),
                        ExpressionUtils.as(
                                select(
                                        subMember.age.max()
                                ).from(subMember)
                                , "age"
                        )
                        )
                )
                .from(member)
                .fetch();

        for (UserDto result : results) {
            System.out.println("result = " + result);
        }
    }



    @Test
    public void findDtoByQueryProjection() {
        List<MemberDto> results = queryFactory.select(
                new QMemberDto(member.username, member.age)
        ).from(member).fetch();

        for (MemberDto result : results) {

            System.out.println("result = " + result);
        }
    }

    @Test
    public void dynamicQuery_BooleanBuilder(){
        String username = "member1";
        Integer age = 10;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (username != null) {
            booleanBuilder.and(member.username.eq(username));
        }

        if (age != null) {
            booleanBuilder.and(member.age.eq(age));
        }


        List<Member> result = queryFactory.select(member)
                .from(member)
                .where(booleanBuilder)
                .fetch();

        for (Member member1 : result) {
            System.out.println("result = " + result);
        }
    }

    @Test
    public void dynamicQuery_where(){
        String username = "member1";
        Integer age = null;

        List<Member> rsults = queryFactory.selectFrom(member)
                .where(usernameEq(username), ageEq(age))
                .fetch();

        for (Member rsult : rsults) {
            System.out.println("rsult = " + rsult);
        }
    }

    private Predicate usernameEq(String username) {
       return username != null ? member.username.eq(username) : null;
    }

    private Predicate ageEq(Integer age) {
        return age != null ? member.age.eq(age) : null;
    }

    @Test
    public void bulkUpdate(){
        long count = queryFactory.update(member)
                .set(member.username, "비회원")
                .where(member.age.gt(10))
                .execute();

        List<Member> allMembers = queryFactory.selectFrom(member).fetch();
        for (Member allMember : allMembers) {
            System.out.println("allMember = " + allMember);
        }

        em.flush();
        em.clear();

        List<Member> allMembers2 = queryFactory.selectFrom(member).fetch();
        for (Member allMember : allMembers2) {
            System.out.println("### allMembe2 = " + allMember);
        }
    }

    /*
     모든 회원의 나이를 1살 더하기 벌크 update
     */
    @Test
    public void bulkAddUpdate(){
        long count = queryFactory.update(member)
                .set(member.age, member.age.add(1))
                .execute();
        em.flush();
        em.clear();

        List<Member> allMembers = queryFactory.selectFrom(member).fetch();
        for (Member allMember : allMembers) {
            System.out.println("### allMember = " + allMember);
        }

    }


    @Test
    public void sqlFunction(){
        List<String> results = queryFactory.select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})",
                        member.username, "member", "MEM"))
                .from(member).fetch();
        for (String result : results) {
            System.out.println("result = " + result);
        }    

    }

    @Test
    public void seqFunction2(){
        List<Member> results = queryFactory.selectFrom(member)
                //.where(Expressions.stringTemplate("function('lower', {0})", member.username).eq("member4"))
                .where(member.username.lower().eq("member4"))
                .fetch();

        for (Member result : results) {
            System.out.println("result = " + result);
        }
    }

    @Test
    public void searchQ(){
        MemberSearchCondition memberSearchCondition = new MemberSearchCondition();
        memberSearchCondition.setAgeGoe(35);
        memberSearchCondition.setAgeLoe(40);

        MemberJpaRepository memberJpaRepository = new MemberJpaRepository(em, queryFactory);
        List<MemberTeamDto> reuslts = memberJpaRepository.searchByBuilder(memberSearchCondition);
        for (MemberTeamDto reuslt : reuslts) {
            System.out.println("reuslt = " + reuslt);
        }
    }
    
    @Test
    public void searchByWhere(){
        MemberSearchCondition memberSearchCondition = new MemberSearchCondition();
        memberSearchCondition.setAgeGoe(35);
        memberSearchCondition.setAgeLoe(40);
        
        MemberJpaRepository memberJpaRepository = new MemberJpaRepository(em, queryFactory);
        List<MemberTeamDto> results = memberJpaRepository.searchByWhere(memberSearchCondition);
        for (MemberTeamDto result : results) {
            System.out.println("result = " + result);
        }

    }
    
}