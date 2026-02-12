package com.enzo.yxzapp.dto.oficina;

import com.enzo.yxzapp.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OficinaResponse(
        Long id,
        String escola,
        String cidade,
        LocalDate data,
        TipoOficina tipo,
        String contatoEscola,
        Segmento segmento,
        Turno turno,
        Turma turma,
        StatusOficina status,
        List<String> instrutores,
        Integer avaliacaoEscola,
        Integer quantitativoAluno,   // pode ser null
        String acompanhanteTurma,    // pode ser null
        String criadorNome,
        CorAdministradora corCriador,
        LocalDateTime dataCriacao,
        String ultimoAtualizadorNome,
        LocalDateTime dataAtualizacao
) {}