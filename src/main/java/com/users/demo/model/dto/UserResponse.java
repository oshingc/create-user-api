package com.users.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.users.demo.model.entity.Phone;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private LocalDateTime created;
    @JsonProperty("modified")
    private LocalDateTime updated;
    private String token;

    @JsonProperty("last_login")
    private LocalDateTime lastLogin;

    @JsonProperty("isactive")
    private Boolean isActive;

    private List<PhoneResponse> phones;

    public UserResponse(UUID id, String name, String email, LocalDateTime created, LocalDateTime updated,
                        String token, Boolean isActive, LocalDateTime lastLogin, List<PhoneResponse> phones) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.created = created;
        this.updated = updated;
        this.token = token;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
        this.phones = phones;
    }

}
