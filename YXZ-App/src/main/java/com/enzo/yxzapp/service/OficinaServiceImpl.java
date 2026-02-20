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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

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

        // Atualizar campos (PATCH parcial - só atualiza se vier preenchido)

        // Status
        if (req.status() != null) {
            oficina.setStatus(req.status());
        }

        // Instrutores
        if (req.instrutores() != null) {
            oficina.setInstrutores(req.instrutores());
        }

        // Avaliação da escola (1-10)
        if (req.avaliacaoEscola() != null) {
            // Validação extra (além do @Min @Max)
            if (req.avaliacaoEscola() < 1 || req.avaliacaoEscola() > 10) {
                throw new BadRequestException("Avaliação deve ser entre 1 e 10");
            }
            oficina.setAvaliacaoEscola(req.avaliacaoEscola());
        }

        // Quantitativo de alunos
        if (req.quantitativoAluno() != null) {
            if (req.quantitativoAluno() < 0) {
                throw new BadRequestException("Quantitativo de alunos não pode ser negativo");
            }
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
    public Page<OficinaResponse> listarTodas(Pageable pageable) {
        return oficinaRepository.findAll(pageable)
                .map(OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OficinaResponse> filtrarPorTipo(TipoOficina tipo, Pageable pageable) {
        return oficinaRepository.findByTipo(tipo, pageable)
                .map(OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OficinaResponse> filtrarPorCidade(String cidade, Pageable pageable) {
        return oficinaRepository.findByCidadeContainingIgnoreCase(cidade, pageable)
                .map(OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OficinaResponse> filtrarPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return oficinaRepository.findByDataBetween(dataInicio, dataFim, pageable)
                .map(OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OficinaResponse> filtrarPorData(LocalDate data, Pageable pageable) {
        return oficinaRepository.findByData(data, pageable)
                .map(OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OficinaResponse> filtrarCombinado(TipoOficina tipo, String cidade,
                                                  LocalDate dataInicio, LocalDate dataFim,
                                                  Pageable pageable) {
        return oficinaRepository.findByFiltros(tipo, cidade, dataInicio, dataFim, pageable)
                .map(OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OficinaResponse> filtrarPorPeriodoMercadologico(Pageable pageable) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusMonths(1).withDayOfMonth(21);
        LocalDate fim = hoje.withDayOfMonth(20);

        return oficinaRepository.findByDataBetween(inicio, fim, pageable)
                .map(OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OficinaResponse> filtrarPorPeriodoMensal(Pageable pageable) {
        YearMonth mesAtual = YearMonth.now();
        LocalDate inicio = mesAtual.atDay(1);
        LocalDate fim = mesAtual.atEndOfMonth();

        return oficinaRepository.findByDataBetween(inicio, fim, pageable)
                .map(OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OficinaResponse> filtrarPorStatus(StatusOficina status, Pageable pageable) {
        return oficinaRepository.findByStatus(status, pageable)
                .map(OficinaResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OficinaResponse> filtrarPorCor(CorAdministradora cor, Pageable pageable) {
        return oficinaRepository.findByCorCriador(cor, pageable)
                .map(OficinaResponse::fromEntity);
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
