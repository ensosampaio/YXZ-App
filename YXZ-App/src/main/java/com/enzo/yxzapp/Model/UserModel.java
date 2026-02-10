package com.enzo.yxzapp.Model;

import com.enzo.yxzapp.Enum.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "users")
public class UserModel {

    private Long id;

    private String name;

    private String email;

    private String passwordHash;

    private UserRole role;

    private AdminColor adminColor;

    private boolean enabled = true;

    private Instant createdAt;
}
