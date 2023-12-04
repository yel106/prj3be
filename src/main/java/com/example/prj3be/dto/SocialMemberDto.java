package com.example.prj3be.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SocialMemberDto {
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
