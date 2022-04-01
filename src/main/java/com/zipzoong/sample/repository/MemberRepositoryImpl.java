package com.zipzoong.sample.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipzoong.sample.dto.MemberSearchCondition;
import com.zipzoong.sample.dto.MemberTeamDto;
import com.zipzoong.sample.dto.QMemberTeamDto;
import com.zipzoong.sample.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.zipzoong.sample.entity.QMember.member;
import static com.zipzoong.sample.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    public final EntityManager em;
    public final JPAQueryFactory queryFactory;

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition memberSearchCondition) {
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
                        ageGoe(memberSearchCondition.getAgeGoe()),
                        ageLoe(memberSearchCondition.getAgeLoe())
                )
                .fetch();
    }

    private BooleanExpression userNameEq(String username) {
        return hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamnName) {
        return hasText(teamnName) ? team.name.eq(teamnName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition,
                                                Pageable pageable) {
        QueryResults<MemberTeamDto> results = queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(userNameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamnName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplexV2(MemberSearchCondition condition,
                                                 Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(userNameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamnName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where(userNameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamnName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .fetchCount();
        return new PageImpl<>(content, pageable, total);
    }

    /*
      토탈이 필요 없을 경우(제일 마지막) 은 Count 쿼리가 안나간다.
     */
    @Override
    public Page<MemberTeamDto> searchPageComplexV3(MemberSearchCondition condition,
                                                 Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(userNameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamnName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Member> countQuery =  queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where(userNameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamnName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()));

        return
                //PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch());
                PageableExecutionUtils.getPage(content, pageable,countQuery::fetchCount);
    }

}
