package com.zipzoong.sample.controller;

import com.zipzoong.sample.dto.MemberDto;
import com.zipzoong.sample.dto.MemberSearchCondition;
import com.zipzoong.sample.dto.MemberTeamDto;
import com.zipzoong.sample.entity.Member;
import com.zipzoong.sample.repository.MemberJpaRepository;
import com.zipzoong.sample.repository.MemberQueryRepository;
import com.zipzoong.sample.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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

    /*
       QueryDsl Paging 처리
       조회 와 total 쿼리를 같이 하므로 성능이 느릴 수 있다.
     */
    @GetMapping("/v1/searchMembers")
    public Page<MemberTeamDto> searchMembers(MemberSearchCondition condition, Pageable pageable){
        return memberRepository.searchPageSimple(condition, pageable);
    }

    /*
       QueryDsl Paging 처리
       total 쿼리 분리해서 성능 향상
       마지막 조회 시도 total 쿼리 나간다..
     */
    @GetMapping("/v2/searchMembers")
    public Page<MemberTeamDto> searchMembersV2(MemberSearchCondition condition, Pageable pageable){
        return memberRepository.searchPageComplexV2(condition, pageable);
    }

    /*
           QueryDsl Paging 처리
           Jpa가 제공하는 성능 최적화.
           토탈이 필요 없을 경우(제일 마지막) 은 Count 쿼리가 안나간다.
           이거 쓰면 된다...
         */
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

    @GetMapping("/v1/findMem/{id}")
    public Member findMem(@PathVariable("id") Long id){
        return memberRepository.findById(id).get();
    }

    @GetMapping("/v1/saveMem")
    public boolean saveMem(String username, int age){
        Member member = memberRepository.save(new Member(username, age));
        return member != null ? true : false;
    }

    /*
      Paging , Spring JPA
      MemberDto 편하게 쓰기
    */
    @GetMapping("/v1/findAllMem")
    public Page<Member> findAllMem(Pageable pageable){
        Page<Member> members = memberRepository.findAll(pageable);
        Page<MemberDto> memberDtos = members.map(m -> new MemberDto(m));
        return memberRepository.findAll(pageable);
    }


}
