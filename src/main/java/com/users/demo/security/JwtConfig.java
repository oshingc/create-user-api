package com.users.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtConfig {
    private final List<String> publicRoutes;

    public JwtConfig(@Value("${jwt.public-routes}") String routes) {
        this.publicRoutes = Arrays.asList(routes.split(","));
    }

    public List<String> getPublicRoutes() {
        return publicRoutes;
    }
}
