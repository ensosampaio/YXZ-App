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

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserResponse create(CreateUserRequest req) {
        if (userRepository.existsByEmail(req.email())) {
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
        user.setNome(req.nome());
        user.setEmail(req.email());
        user.setSenha(encoder.encode(req.senha()));
        user.setRole(req.role());
        user.setAtivo(true);

        if (req.role() == Role.ADMIN) {
            user.setCorAdministradora(req.corAdministradora());
        } else {
            user.setCorAdministradora(null);
        }

        User saved = userRepository.save(user);

        return UserResponse.fromEntity(saved);
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (req.nome() != null && !req.nome().isBlank()) {
            user.setNome(req.nome().trim());
        }

        if (req.ativo() != null) {
            user.setAtivo(req.ativo());
        }

        Role newRole = (req.role() != null) ? req.role() : user.getRole();
        CorAdministradora newColor = (req.role() != null || req.corAdministradora() != null)
                ? req.corAdministradora()
                : user.getCorAdministradora();

        if (user.getRole() == Role.ROOT && newRole != Role.ROOT) {
            throw new BadRequestException("Não é permitido alterar o usuário ROOT");
        }
        if (newRole == Role.ROOT && user.getRole() != Role.ROOT) {
            throw new BadRequestException("Não é permitido promover usuário para ROOT");
        }

        // Regras de cor
        if (newRole == Role.ADMIN && newColor == null) {
            throw new BadRequestException("ADMIN precisa ter uma corAdministradora");
        }
        if (newRole != Role.ADMIN) {
            newColor = null;
        }

        user.setRole(newRole);
        user.setCorAdministradora(newColor);

        User updated = userRepository.save(user);
        return UserResponse.fromEntity(updated);
    }

    @Override
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
}
