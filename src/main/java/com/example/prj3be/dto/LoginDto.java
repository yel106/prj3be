package com.example.prj3be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {
    @NotBlank
    private String logId;
    @NotBlank
    @Size(min=4, max=16)
    private String password;
}
