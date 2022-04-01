package com.zipzoong.sample.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

// @MappedSuperclass 가 있어야 속성 값이 상속한 Entity에 먹힌다.
@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    //저장하기 전에 자동으로 동작
    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        //조회할 때 null 있으면 처리하기 귀찮으니 그냥 넣어 놓는다.
        updateDate = now;
    }

    //Update 하기 전에 자동으로 동작
    @PreUpdate
    public void preUpdate(){
        updateDate = LocalDateTime.now();
    }

}
