package com.enzo.yxzapp.service;

import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.user.CreateUserRequest;
import com.enzo.yxzapp.dto.user.UpdateUserRequest;
import com.enzo.yxzapp.dto.user.UserResponse;
import com.enzo.yxzapp.repository.UserRepository;
import org.springframework.data.domain.Pageable;

public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse create(CreateUserRequest req) {
        return null;
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
