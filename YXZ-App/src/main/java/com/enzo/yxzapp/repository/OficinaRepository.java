package com.enzo.yxzapp.repository;

import com.enzo.yxzapp.enums.CorAdministradora;
import com.enzo.yxzapp.enums.StatusOficina;
import com.enzo.yxzapp.enums.TipoOficina;
import com.enzo.yxzapp.model.Oficina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OficinaRepository extends JpaRepository<Oficina, Long> {
    // Filtro por tipo com paginação
    Page<Oficina> findByTipo(TipoOficina tipo, Pageable pageable);

    // Filtro por cidade (case-insensitive, busca parcial) com paginação
    @Query("SELECT o FROM Oficina o WHERE LOWER(o.cidade) LIKE LOWER(CONCAT('%', :cidade, '%'))")
    Page<Oficina> findByCidadeContainingIgnoreCase(@Param("cidade") String cidade, Pageable pageable);

    // Filtro por período (entre duas datas) com paginação
    Page<Oficina> findByDataBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    // Filtro por data específica com paginação
    Page<Oficina> findByData(LocalDate data, Pageable pageable);

    // Query customizada para combinar múltiplos filtros COM PAGINAÇÃO
    @Query("SELECT o FROM Oficina o WHERE " +
            "(:tipo IS NULL OR o.tipo = :tipo) AND " +
            "(:cidade IS NULL OR LOWER(o.cidade) LIKE LOWER(CONCAT('%', :cidade, '%'))) AND " +
            "(:dataInicio IS NULL OR o.data >= :dataInicio) AND " +
            "(:dataFim IS NULL OR o.data <= :dataFim)")
    Page<Oficina> findByFiltros(
            @Param("tipo") TipoOficina tipo,
            @Param("cidade") String cidade,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable
    );

    // Buscar oficinas de um mês específico (para calendário - sem paginação)
    @Query("SELECT o FROM Oficina o WHERE YEAR(o.data) = :ano AND MONTH(o.data) = :mes")
    List<Oficina> findByMes(@Param("ano") int ano, @Param("mes") int mes);

    // Filtro por status
    Page<Oficina> findByStatus(StatusOficina status, Pageable pageable);

    // Filtro por cor do criador
    Page<Oficina> findByCorCriador(CorAdministradora cor, Pageable pageable);
}


