package com.enzo.yxzapp.service;

import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.oficina.CreateOficinaRequest;
import com.enzo.yxzapp.dto.oficina.OficinaResponse;
import com.enzo.yxzapp.dto.oficina.UpdateViaModalRequest;
import com.enzo.yxzapp.enums.Role;
import com.enzo.yxzapp.enums.StatusOficina;
import com.enzo.yxzapp.exception.BadRequestException;
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
        // Buscar usuário logado
        User criador = getUsuarioLogado();

        // Validar que é ADMIN
        if (criador.getRole() != Role.ADMIN) {
            throw new BadRequestException("Apenas administradores podem criar oficinas");
        }

        // Validar que ADMIN tem cor
        if (criador.getCorAdministradora() == null) {
            throw new BadRequestException("Administrador deve ter uma cor definida");
        }

        // Criar oficina
        Oficina oficina = new Oficina();
        oficina.setEscola(req.escola());
        oficina.setCidade(req.cidade());
        oficina.setData(req.data());
        oficina.setTipo(req.tipo());
        oficina.setContatoEscola(req.contatoEscola());
        oficina.setSegmento(req.segmento());
        oficina.setTurno(req.turno());
        oficina.setTurma(req.turma());

        // Status inicial
        oficina.setStatus(StatusOficina.AGENDADA);

        // Relacionamento com criador
        oficina.setCriador(criador);

        // Salvar
        Oficina salva = oficinaRepository.save(oficina);

        // Retornar response
        return OficinaResponse.fromEntity(salva);
    }


    @Override
    public OficinaResponse update(Long id, UpdateViaModalRequest req) {
        return null;
    }

    @Override
    public PageResponse<OficinaResponse> list(Pageable pageable) {
        return null;
    }

    @Override
    public OficinaResponse getById(Long id) {
        return null;
    }


    private User getUsuarioLogado() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
