package com.zipzoong.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipzoong.dto.MemberSearchCondition;
import com.zipzoong.dto.MemberTeamDto;
import com.zipzoong.dto.QMemberTeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.zipzoong.entity.QMember.member;
import static com.zipzoong.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

/*
  특화된 쿼리는 굳이 Custom 을 만들어 impl 하지 않고 별도로 만들어 사용한다.

 */
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    public final EntityManager em;
    public final JPAQueryFactory queryFactory;

    public List<MemberTeamDto> searchSp(MemberSearchCondition memberSearchCondition) {
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
