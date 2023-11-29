package com.example.prj3be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @NotBlank
    private String logId;
    @NotBlank
    @Size(min=4, max=16)
    private String password;
}
