package com.example.prj3be.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MemberAuthDto implements Serializable {
    private static final long serialVersionUID = 1L; // serialVersionUID 추가
    private String logId;
    private String role;
    @JsonCreator
    public MemberAuthDto() {
        this.logId = logId;
        this.role = role;
    }
}
