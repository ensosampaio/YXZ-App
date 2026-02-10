package com.enzo.yxzapp.Model;

import com.enzo.yxzapp.Enum.AdminColor;
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

    private Instant updatedAt;

    public void touchUpdated() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public AdminColor getAdminColor() {
        return adminColor;
    }

    public void setAdminColor(AdminColor adminColor) {
        this.adminColor = adminColor;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
