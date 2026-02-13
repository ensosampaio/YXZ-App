package com.enzo.yxzapp.service;

import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.user.CreateUserRequest;
import com.enzo.yxzapp.dto.user.UpdateUserRequest;
import com.enzo.yxzapp.dto.user.UserResponse;
import com.enzo.yxzapp.enums.Role;
import com.enzo.yxzapp.exception.BadRequestException;
import com.enzo.yxzapp.model.User;
import com.enzo.yxzapp.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
        return null;
    }

    @Override
    public PageResponse<UserResponse> list(Pageable pageable) {
        return null;
    }

    @Override
    public UserResponse getById(Long id) {
        return null;
    }
}
