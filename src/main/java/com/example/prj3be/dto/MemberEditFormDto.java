package com.example.prj3be.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberEditFormDto {
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private String email;
    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String address;
}
