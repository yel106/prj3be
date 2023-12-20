package com.example.prj3be.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberAuthDto implements Serializable {
    private static final long serialVersionUID = 1L; // serialVersionUID 추가
    private String logId;
    private String role;
}
