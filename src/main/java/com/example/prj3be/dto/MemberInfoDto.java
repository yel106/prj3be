package com.example.prj3be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MemberInfoDto {
    private String logId;
    private String name;
    private String email;
    private String address;
    private String gender;
    private String role;
}
