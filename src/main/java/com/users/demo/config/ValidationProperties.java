package com.users.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ValidationProperties {

    @Value("${validation.email.regex}")
    private String emailRegex;

    @Value("${validation.password.regex}")
    private String passwordRegex;

}

