package com.zipzoong.dto;

import lombok.Data;

@Data
public class MemberSearchCondition {
    private String username;
    private String teamnName;
    private Integer ageGoe;
    private Integer ageLoe;
}
