package com.zipzoong.controller;

import com.zipzoong.dto.MemberDto;
import com.zipzoong.dto.MemberSearchCondition;
import com.zipzoong.dto.MemberTeamDto;
import com.zipzoong.entity.Member;
import com.zipzoong.repository.MemberJpaRepository;
import com.zipzoong.repository.MemberQueryRepository;
import com.zipzoong.repository.MemberRepository;
import com.zipzoong.repository.MemberRepositoryCustom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class memberController {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMember(MemberSearchCondition condition){
       return memberJpaRepository.searchByWhere(condition);
    }

    /*
       Dto 로 변환..
     */
    @GetMapping("/v1/getMemberByName")
    public List<MemberDto> getMemberByName(String username){
        List<Member> members =memberRepository.findByUsername(username);

        List<MemberDto> memberDtos =  members.stream().map(
                        member -> new MemberDto(member.getUsername(), member.getAge())
                ).collect(Collectors.toList());

        for (MemberDto dto : memberDtos) {
            System.out.println("dto = " + dto);
        }

        return memberDtos;
    }

    @GetMapping("/v1/search")
    public List<MemberTeamDto> search(MemberSearchCondition condition){
        return memberRepository.search(condition);
    }

    @GetMapping("/v1/searchSp")
    public List<MemberTeamDto> searchSp(MemberSearchCondition condition){
        return memberQueryRepository.searchSp(condition);
    }

    @GetMapping("/v1/searchMembers")
    public Page<MemberTeamDto> searchMembers(MemberSearchCondition condition, Pageable pageable){
        return memberRepository.searchPageSimple(condition, pageable);
    }

    @GetMapping("/v2/searchMembers")
    public Page<MemberTeamDto> searchMembersV2(MemberSearchCondition condition, Pageable pageable){
        return memberRepository.searchPageComplexV2(condition, pageable);
    }

    @GetMapping("/v3/searchMembers")
    public Page<MemberTeamDto> searchPageComplexV3(MemberSearchCondition condition, Pageable pageable){
        return memberRepository.searchPageComplexV3(condition, pageable);
    }

    /*
       Paging , Spring JPA
       Member --> MemberDto 변환
     */
    @GetMapping("/v1/findSjpaByAgePaging")
    public Page<MemberDto> findSjpaByAgePaging(int age, Pageable pageable){
        Page<Member> members = memberRepository.findSjpaByAge(age, pageable);
        Page<MemberDto> memberDtos = members.map(m -> new MemberDto(m.getUsername(), m.getAge(), null));
        return memberDtos;
    }



}
