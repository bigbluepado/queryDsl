package com.zipzoong.sample.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipzoong.sample.dto.MemberDto;
import com.zipzoong.sample.entity.Member;
import com.zipzoong.sample.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;


    @BeforeEach
    public void before() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


    }

    @Test
    public void test(){
        List<Member> result3 = memberRepository.findByAge(10);
        List<Member> result4 = memberRepository.findByUsernameAndAgeGreaterThan("member1", 5);
        for (Member member : result4) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void testMethod(){
        List<Member> result4 = memberRepository.findZZTop3By();
        for (Member member : result4) {
            System.out.println("member = " + member);

        }
    }

    @Test
    public void testQuery(){
        List<Member> result4 = memberRepository.findUser("member1", 10);
        for (Member member : result4) {
            System.out.println("member = " + member);

        }
    }

    @Test
    public void sjpaPaging(){
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findSjpaByAge(10, pageRequest);

        int totalPages = page.getTotalPages();
        System.out.println("totalPages = " + totalPages);

        long totalElements = page.getTotalElements();
        System.out.println("totalElements = " + totalElements);

        int size = page.getSize();
        System.out.println("size = " + size);

        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }


    }

    @Test
    public void findSjpaSliceByAgeAfter(){

        int pageNem = 0;
        PageRequest pageRequest = PageRequest.of(pageNem, 3, Sort.by(Sort.Direction.DESC, "username"));

        Slice<Member> page = memberRepository.findSjpaSliceByAgeAfter(4, pageRequest);

       // Slice 에는 없는 기능
       // int totalPages = page.getTotalPages();
       // System.out.println("totalPages = " + totalPages);

        // Slice 에는 없는 기능
        //long totalElements = page.getTotalElements();
        //System.out.println("totalElements = " + totalElements);

        int nowNum = page.getNumber();
        System.out.println("nowNum = " + nowNum);


        int nowEleNum = page.getNumberOfElements();
        System.out.println("nowEleNum = " + nowEleNum);

        int size = page.getSize();
        System.out.println("size = " + size);

        boolean hasNext = page.hasNext();
        System.out.println("hasNext = " + hasNext);

        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }

        if(hasNext){
            pageNem = 1;

            PageRequest pageRequest1 = PageRequest.of(pageNem, 3, Sort.by(Sort.Direction.DESC, "username"));

            Slice<Member> page1 = memberRepository.findSjpaSliceByAgeAfter(4, pageRequest1);

            List<Member> content1 = page1.getContent();

            for (Member member : content1) {
                System.out.println("--> next member = " + member);
            }


        }

    }

    @Test
    public void updateBulk(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));
        //when
        int resultCount = memberRepository.updateBulk(20);
        System.out.println("resultCount = " + resultCount);
        //em.flush();
        //em.clear();

        List<Member> member5 = memberRepository.findByUsername("member5");

        System.out.println("member5 =" + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }



}