package com.example.prj3be.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

// build.gradle에 query dsl 의존성 추가해야 어노테이션 작동하는 이유?
@MappedSuperclass // 공통 매핑정보를 코드를 중복하지 않고, 상속받는 자식클래스에서 재사용할수 있음
@Getter
public class JpaBaseEntity {
    @Column(updatable = false)
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        updateDate = now;
    }
    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
