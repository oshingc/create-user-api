package com.users.demo.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoginResponse {

    private UUID id;
    private String name;
    private String email;
    private String token;
    private LocalDateTime lastLogin;
    private Boolean isActive;

    public LoginResponse(UUID id, String name, String email, String token, LocalDateTime lastLogin, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.token = token;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }

    // Getters y setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
