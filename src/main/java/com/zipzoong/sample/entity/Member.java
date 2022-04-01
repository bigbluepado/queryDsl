package com.zipzoong.sample.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//객체를 찍으면 아래의 변수가 찍힌다. 편할려고 사용
//team 은 연관관계가 있어 찍으면 안된다.!! 무한루프...
@ToString(of = {"id", "username", "age"})
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    //JPA는 agr 없은 기본 생성자를 protected 까지만 만들어야 한다.
    //lombok의 @NoArgsConstructor(access = AccessLevel.PROTECTED) 대체 가능
    //protected Member(){}

    public Member(String username){
        this(username, 0);
    }

    public Member(String username, int age){
        this(username, age, null);
    }

    public Member(String username, int age, Team team){
        this.username = username;
        this.age = age;
        if (team != null){
            changeTeam(team);
        }
    }

    //연관관계를 변경할 수 있는 메소드를 만들어 줘야 함.
    private void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }



}
