package com.enzo.yxzapp.dto.user;

import com.enzo.yxzapp.enums.CorAdministradora;
import com.enzo.yxzapp.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String nome,
        String email,
        Role role,
        CorAdministradora corAdministradora,
        boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}