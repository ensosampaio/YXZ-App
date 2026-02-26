package com.enzo.yxzapp.service;

import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.oficina.CreateOficinaRequest;
import com.enzo.yxzapp.dto.oficina.OficinaResponse;
import com.enzo.yxzapp.dto.oficina.UpdateViaModalRequest;
import com.enzo.yxzapp.enums.CorAdministradora;
import com.enzo.yxzapp.enums.Role;
import com.enzo.yxzapp.enums.StatusOficina;
import com.enzo.yxzapp.enums.TipoOficina;
import com.enzo.yxzapp.exception.BadRequestException;
import com.enzo.yxzapp.exception.NotFoundException;
import com.enzo.yxzapp.model.Oficina;
import com.enzo.yxzapp.model.User;
import com.enzo.yxzapp.repository.OficinaRepository;
import com.enzo.yxzapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OficinaServiceImpl implements OficinaService {

    private final OficinaRepository oficinaRepository;
    private final UserRepository userRepository;

    public OficinaServiceImpl(OficinaRepository oficinaRepository,  UserRepository userRepository) {
        this.oficinaRepository = oficinaRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OficinaResponse create(CreateOficinaRequest req) {
        User criador = getUsuarioLogado();

        if (criador.getRole() != Role.ADMIN) {
            throw new BadRequestException("Apenas administradores podem criar oficinas");
        }

        if (criador.getCorAdministradora() == null) {
            throw new BadRequestException("Administrador deve ter uma cor definida");
        }

        Oficina oficina = new Oficina();
        oficina.setEscola(req.escola());
        oficina.setCidade(req.cidade());
        oficina.setData(req.data());
        oficina.setTipo(req.tipo());
        oficina.setContatoEscola(req.contatoEscola());
        oficina.setSegmento(req.segmento());
        oficina.setTurno(req.turno());
        oficina.setTurma(req.turma());
        oficina.setStatus(StatusOficina.AGENDADA);

        // Snapshot do criador
        oficina.setCriadorNome(criador.getNome());
        oficina.setCorCriador(criador.getCorAdministradora());
        oficina.setCriadorId(criador.getId());

        Oficina salva = oficinaRepository.save(oficina);
        return OficinaResponse.fromEntity(salva);
    }


    @Override
    @Transactional
    public OficinaResponse update(Long id, UpdateViaModalRequest req) {
        // Buscar usuário logado
        User atualizador = getUsuarioLogado();

        // Buscar oficina
        Oficina oficina = oficinaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Oficina não encontrada"));

        // --- Atualizar campos (PATCH parcial - só atualiza se não for null) ---

        // Status
        if (req.status() != null) {
            oficina.setStatus(req.status());
        }

        // Motivo de Cancelamento
        if (req.motivoCancelamento() != null) {
            oficina.setMotivoCancelamento(req.motivoCancelamento());
        }

        // REGRA DE NEGÓCIO: Se não estiver cancelada, garantimos que o motivo é nulo
        if (oficina.getStatus() != StatusOficina.CANCELADA) {
            oficina.setMotivoCancelamento(null);
        }

        // Instrutores
        if (req.instrutores() != null) {
            oficina.setInstrutores(req.instrutores());
        }

        // Avaliação da escola (Correção: Adicionado verificação de null)
        if (req.avaliacaoEscola() != null) {
            oficina.setAvaliacaoEscola(req.avaliacaoEscola());
        }

        // Quantitativo de alunos
        if (req.quantitativoAluno() != null) {
            oficina.setQuantitativoAluno(req.quantitativoAluno());
        }

        // Acompanhante da turma
        if (req.acompanhanteTurma() != null && !req.acompanhanteTurma().isBlank()) {
            oficina.setAcompanhanteTurma(req.acompanhanteTurma().trim());
        }

        // Registrar quem atualizou (snapshot)
        oficina.setUltimoAtualizadorNome(atualizador.getNome());
        oficina.setUltimoAtualizadorId(atualizador.getId());

        // Salvar
        Oficina atualizada = oficinaRepository.save(oficina);

        // Retornar response
        return OficinaResponse.fromEntity(atualizada);
    }

    @Override
    public PageResponse<OficinaResponse> list(Pageable pageable) {
        Page<Oficina> page = oficinaRepository.findAll(pageable);
        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public OficinaResponse getById(Long id) {
        return oficinaRepository.findById(id)
                .map(OficinaResponse::fromEntity)
                .orElseThrow(() -> new NotFoundException("Oficina não encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OficinaResponse> filtrarPorTipo(TipoOficina tipo, Pageable pageable) {

        Page<Oficina> page = oficinaRepository.findByTipo(tipo, pageable);

        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OficinaResponse> filtrarPorCidade(String cidade, Pageable pageable) {

        Page<Oficina> page = oficinaRepository
                .findByCidadeContainingIgnoreCase(cidade, pageable);

        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OficinaResponse> filtrarPorPeriodo(
            LocalDate inicio,
            LocalDate fim,
            Pageable pageable) {

        Page<Oficina> page = oficinaRepository
                .findByDataBetween(inicio, fim, pageable);

        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OficinaResponse> filtrarPorData(LocalDate data, Pageable pageable) {

        Page<Oficina> page = oficinaRepository.findByData(data, pageable);

        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OficinaResponse> filtrarCombinado(
            TipoOficina tipo,
            String cidade,
            LocalDate inicio,
            LocalDate fim,
            Pageable pageable) {

        Page<Oficina> page = oficinaRepository
                .findByFiltros(tipo, cidade, inicio, fim, pageable);

        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OficinaResponse> filtrarPorPeriodoMercadologico(Pageable pageable) {

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusMonths(1).withDayOfMonth(21);
        LocalDate fim = hoje.withDayOfMonth(20);

        Page<Oficina> page = oficinaRepository
                .findByDataBetween(inicio, fim, pageable);

        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OficinaResponse> filtrarPorPeriodoMensal(Pageable pageable) {

        YearMonth mesAtual = YearMonth.now();

        Page<Oficina> page = oficinaRepository.findByDataBetween(
                mesAtual.atDay(1),
                mesAtual.atEndOfMonth(),
                pageable
        );

        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OficinaResponse> filtrarPorStatus(StatusOficina status, Pageable pageable) {

        Page<Oficina> page = oficinaRepository.findByStatus(status, pageable);

        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OficinaResponse> filtrarPorCor(CorAdministradora cor, Pageable pageable) {

        Page<Oficina> page = oficinaRepository.findByCorCriador(cor, pageable);

        return PageResponse.fromPage(page, OficinaResponse::fromEntity);
    }

    // ==================== CALENDÁRIO ====================

    @Override
    @Transactional(readOnly = true)
    public List<OficinaResponse> buscarPorMes(int ano, int mes) {
        return oficinaRepository.findByMes(ano, mes)
                .stream()
                .map(OficinaResponse::fromEntity)
                .collect(Collectors.toList());
    }


    private User getUsuarioLogado() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
