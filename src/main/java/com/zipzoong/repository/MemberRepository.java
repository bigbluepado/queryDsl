package com.zipzoong.repository;

import com.zipzoong.dto.MemberDto;
import com.zipzoong.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


/*

   QueryDsl 과 Spring JPA 를 함께 사용할 때 아래처럼 구현한다.

   기본적인 Spring JPA 가 지원하는 것만 사용하고
   Join , Paging  등 별도 query 가 필요하면 QueryDsl 로 작성하면 된다.

   Paging 의 Slice 정도는 쓸만해 보임.

   Dto 만드는 것도 QueryDsl 이 편해 보인다. -> findSjpaSliceByAgeAfter()

 */


/*
  where 지원
  https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation

  메소즈 지원
  조회: find...By ,read...By ,query...By get...By,
    https://docs.spring.io/spring-data/jpa/docs/current/reference/html/ #repositories.query-methods.query-creation
    예:) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다.
    COUNT: count...By 반환타입 long
    EXISTS: exists...By 반환타입 boolean
    삭제: delete...By, remove...By 반환타입 long DISTINCT: findDistinct, findMemberDistinctBy LIMIT: findFirst3, findFirst, findTop, findTop3
    https://docs.spring.io/spring-data/jpa/docs/current/reference/html/ #repositories.limit-query-result
*/

public interface MemberRepository extends JpaRepository<Member , Long> , MemberRepositoryCustom{
    /*
       QueryDsl 은
        MemberRepositoryCustom을 구현한 MemberRepositorylmpl 에 구현되어 있다. !!!
     */

    // 아래는 전부 Spring JPA 를 사용함.
    List<Member> findByUsername(String username);
    List<Member> findByAge(int age);
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    List<Member> findByUsernameOrAgeGreaterThan(String username, int age);
    List<Member> findZZTop3By ();

    //Sprig JPA, Named Query --> 쓸일이 별로 없어 보인다...
    @Query("select m from Member m where m.username =:username and m.age =:age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    //Sprig JPA, Dto 리턴 --> QueryDsl 로 많이 쓴다...
    @Query("select new com.zipzoong.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //Sprig JPA, 여러개 변수 바인딩... 이정도는 쓴다는데.. 그냥 QuseryDsl 로 해도 될 듯.. 근데 이게 간단하긴 해 보이긴 하다.
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    //Spring JPA 로 paging 처리, 사용할 때 PageRequest 에 offset, size, sort 를 지정한다.
    Page<Member> findSjpaByAge(int age, Pageable pageable);

    //Sprig JPA, 모바일에서 다음으로 size의 데이터를 가져 올 때 .. Spring JPA 로 paging 처리
    Slice<Member> findSjpaSliceByAgeAfter(int age, Pageable pageable);

    //Sprig JPA, count Query 분리
    @Query(value = "select m from Member m", countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);

    //Sprig JPA, 벌크 Update
    //벌크 Update 후에는 반드시
    //  em.flush (남아 있는 영속성 데이터를 DB에 반영)
    //  em.clear (영속성 데이터 초기화)  를 해 주자
    //  @Modifying(clearAutomatically = true) 가 flush, clear 자동으로 해 준다.
    @Modifying(clearAutomatically = true)
    @Query(value = "update Member m set m.age = m.age +1 where m.age >= :age ")
    int updateBulk(@Param("age") int age);

}
