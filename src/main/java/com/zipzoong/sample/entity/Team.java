package com.zipzoong.sample.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    //join 될 경우 FK 없는 쪽에 mappedBy를 걸어준다.
    // Member 테이블이 Many 쪽이므로 FK 가 있다. PK는 One 쪽이다.
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();


    //JPA는 agr 없은 기본 생성자를 protected 까지만 만들어야 한다.
    //lombok의 @NoArgsConstructor(access = AccessLevel.PROTECTED) 대체 가능
    //protected Team(){}

    public Team(String name){
        this.name = name;
    }

}
