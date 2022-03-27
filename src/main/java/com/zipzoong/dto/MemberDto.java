package com.zipzoong.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zipzoong.entity.Team;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String username;
    private int age;
    private String teamName;

    @QueryProjection
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public MemberDto(String username, int age, String teamName) {
        this.username = username;
        this.age = age;
        this.teamName = teamName;
    }

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
