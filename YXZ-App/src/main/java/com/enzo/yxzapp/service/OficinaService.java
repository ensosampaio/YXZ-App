package com.enzo.yxzapp.service;

import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.oficina.CreateOficinaRequest;
import com.enzo.yxzapp.dto.oficina.OficinaResponse;
import com.enzo.yxzapp.dto.oficina.UpdateViaModalRequest;
import com.enzo.yxzapp.enums.CorAdministradora;
import com.enzo.yxzapp.enums.StatusOficina;
import com.enzo.yxzapp.enums.TipoOficina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;


public interface OficinaService {
    OficinaResponse create(CreateOficinaRequest req);
    OficinaResponse update(Long id, UpdateViaModalRequest req);
    PageResponse<OficinaResponse> list(Pageable pageable);
    OficinaResponse getById(Long id);

    // Listagem e Filtros
    Page<OficinaResponse> listarTodas(Pageable pageable);
    Page<OficinaResponse> filtrarPorTipo(TipoOficina tipo, Pageable pageable);
    Page<OficinaResponse> filtrarPorCidade(String cidade, Pageable pageable);
    Page<OficinaResponse> filtrarPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);
    Page<OficinaResponse> filtrarPorData(LocalDate data, Pageable pageable);
    Page<OficinaResponse> filtrarCombinado(TipoOficina tipo, String cidade,
                                           LocalDate dataInicio, LocalDate dataFim,
                                           Pageable pageable);
    Page<OficinaResponse> filtrarPorPeriodoMercadologico(Pageable pageable);
    Page<OficinaResponse> filtrarPorPeriodoMensal(Pageable pageable);
    Page<OficinaResponse> filtrarPorStatus(StatusOficina status, Pageable pageable);
    Page<OficinaResponse> filtrarPorCor(CorAdministradora cor, Pageable pageable);

    // Calendário
    List<OficinaResponse> buscarPorMes(int ano, int mes);
}
