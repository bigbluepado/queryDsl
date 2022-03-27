package com.zipzoong.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipzoong.dto.MemberSearchCondition;
import com.zipzoong.dto.MemberTeamDto;
import com.zipzoong.dto.QMemberTeamDto;
import com.zipzoong.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.zipzoong.entity.QMember.member;
import static com.zipzoong.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;


/*

   순수한 JPA 로 구현할 때 아래 처럼 구현한다.

 */
@Repository
@RequiredArgsConstructor

public class MemberJpaRepository {
    public final EntityManager em;
    public final JPAQueryFactory queryFactory;

    /*
    //@RequiredArgsConstructor => rombok 이 대신 작성해 줌
    public MemberRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
    }
    */


    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public Optional<Member> findById(Long id) {
        Member result = em.find(Member.class, id);
        return Optional.ofNullable(result);
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }


    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(condition.getTeamnName())) {
            builder.and(member.username.eq(condition.getUsername()));
        }

        if (hasText(condition.getTeamnName())) {
            builder.and((team.name.eq(condition.getTeamnName())));
        }

        if (condition.getAgeGoe() != null) {
            builder.and(member.age.goe(condition.getAgeGoe()));
        }

        if (condition.getAgeLoe() != null) {
            builder.and(member.age.loe(condition.getAgeLoe()));
        }


        return queryFactory.select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(builder)
                .fetch();
    }

    public List<MemberTeamDto> searchByWhere(MemberSearchCondition memberSearchCondition) {
        return queryFactory
                .select(
                        new QMemberTeamDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")
                        )
                ).from(member)
                .leftJoin(member.team, team)
                .where(
                        userNameEq(memberSearchCondition.getUsername()),
                        teamNameEq(memberSearchCondition.getTeamnName()),
                        ageGeo(memberSearchCondition.getAgeGoe()),
                        ageLeo(memberSearchCondition.getAgeLoe())
                )
                .fetch();
    }

    private BooleanExpression userNameEq(String username) {
        return hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamnName) {
        return hasText(teamnName) ? team.name.eq(teamnName) : null;
    }

    private BooleanExpression ageGeo(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLeo(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }


}
