package com.enzo.yxzapp.service;

import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.oficina.CreateOficinaRequest;
import com.enzo.yxzapp.dto.oficina.OficinaResponse;
import com.enzo.yxzapp.dto.oficina.UpdateViaModalRequest;
import com.enzo.yxzapp.enums.Role;
import com.enzo.yxzapp.enums.StatusOficina;
import com.enzo.yxzapp.exception.BadRequestException;
import com.enzo.yxzapp.exception.NotFoundException;
import com.enzo.yxzapp.model.Oficina;
import com.enzo.yxzapp.model.User;
import com.enzo.yxzapp.repository.OficinaRepository;
import com.enzo.yxzapp.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

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
        return null;
    }

    @Override
    public OficinaResponse getById(Long id) {
        return OficinaResponse.fromEntity(oficinaRepository.findById(id).orElseThrow(() -> new NotFoundException("Oficina inexistente")));
    }


    private User getUsuarioLogado() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
