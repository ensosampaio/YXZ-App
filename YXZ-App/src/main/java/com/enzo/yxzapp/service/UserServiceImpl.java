package com.enzo.yxzapp.service;

import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.user.CreateUserRequest;
import com.enzo.yxzapp.dto.user.UpdateUserRequest;
import com.enzo.yxzapp.dto.user.UserResponse;
import com.enzo.yxzapp.enums.CorAdministradora;
import com.enzo.yxzapp.enums.Role;
import com.enzo.yxzapp.exception.BadRequestException;
import com.enzo.yxzapp.exception.NotFoundException;
import com.enzo.yxzapp.model.User;
import com.enzo.yxzapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserResponse create(CreateUserRequest req) {
        if (userRepository.existsByEmail(normalizeEmail(req.email()))) {
            throw new BadRequestException("Usuário já cadastrado");
        }

        if (req.role() == Role.ROOT) {
            throw new BadRequestException("Não é permitido criar outro usuário ROOT");
        }

        if (req.role() == Role.ADMIN && req.corAdministradora() == null) {
            throw new BadRequestException("ADMIN precisa ter uma corAdministradora");
        }

        if (req.role() != Role.ADMIN && req.corAdministradora() != null) {
            throw new BadRequestException("Somente ADMIN pode ter corAdministradora");
        }

        User user = new User();
        user.setNome(normalizeName(req.nome()));
        user.setEmail(normalizeEmail(req.email()));
        user.setSenha(encoder.encode(req.senha()));
        user.setRole(req.role());
        user.setAtivo(true);
        user.setCorAdministradora(req.role() == Role.ADMIN ? req.corAdministradora() : null);

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        boolean isRootUser = user.getRole() == Role.ROOT;

        // nome (PATCH parcial)
        if (req.nome() != null && !req.nome().isBlank()) {
            user.setNome(normalizeName(req.nome()));
        }

        // ativo (PATCH parcial)
        if (req.ativo() != null) {
            if (isRootUser && !req.ativo()) {
                throw new BadRequestException("Não é permitido desativar o usuário ROOT");
            }
            user.setAtivo(req.ativo());
        }

        // role/cor (PATCH parcial) — valida conjunto
        Role newRole = (req.role() != null) ? req.role() : user.getRole();

        // Bloqueios envolvendo ROOT
        if (newRole == Role.ROOT && !isRootUser) {
            throw new BadRequestException("Não é permitido promover usuário para ROOT");
        }
        if (isRootUser && newRole != Role.ROOT) {
            throw new BadRequestException("Não é permitido alterar o role do usuário ROOT");
        }

        CorAdministradora newColor;
        if (req.role() != null || req.corAdministradora() != null) {
            newColor = req.corAdministradora();
        } else {
            newColor = user.getCorAdministradora();
        }

        if (newRole == Role.ADMIN && newColor == null) {
            throw new BadRequestException("ADMIN precisa ter uma corAdministradora");
        }
        if (newRole != Role.ADMIN) {
            newColor = null; // garante consistência
        }

        user.setRole(newRole);
        user.setCorAdministradora(newColor);

        User updated = userRepository.save(user);
        return UserResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public PageResponse<UserResponse> list(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return PageResponse.fromPage(page, UserResponse::fromEntity);
    }

    @Override
    public UserResponse getById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    private String normalizeName(String nome) {
        if (nome == null) return null;
        return nome.trim().replaceAll("\\s+", " ");
    }
}

