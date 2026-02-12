package com.enzo.yxzapp.dto.oficina;

import com.enzo.yxzapp.enums.StatusOficina;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

public record UpdateViaModalRequest(
        StatusOficina status,
        List<String> instrutores,
        @Min(1) @Max(10) Integer avaliacaoEscola,
        Integer quantitativoAluno,
        String acompanhanteTurma
) {}