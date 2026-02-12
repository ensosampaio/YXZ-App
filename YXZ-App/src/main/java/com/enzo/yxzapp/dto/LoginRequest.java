package com.enzo.yxzapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
)
{
}
