package com.users.demo.service;

import com.users.demo.model.dto.*;
import com.users.demo.model.entity.User;
import com.users.demo.repository.UserRepository;
import com.users.demo.security.JwtUtil;
import com.users.demo.util.Messages;
import com.users.demo.validation.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest request) {

        // Validación de email y password con regex configurable
        userValidator.validate(request);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(Messages.EMAIL_ALREADY_REGISTERED);
        }

        // Construcción del usuario
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(request.getPassword()));

        user.setCreated(LocalDateTime.now());
        user.setIsActive(true);

        // Generar token JWT
        String token = jwtUtil.generateToken(request.getEmail());
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(Messages.TOKEN_GENERATION_FAILED);
        }
        user.setToken(token);

        if (request.getPhones() != null) {
            request.getPhones().forEach(phone -> phone.setUser(user));
            user.setPhones(request.getPhones());
        }

        User savedUser = userRepository.save(user);

        List<PhoneResponse> phoneResponses = savedUser.getPhones().stream()
                .map(p -> new PhoneResponse(p.getNumber(), p.getCityCode(), p.getCountryCode()))
                .collect(Collectors.toList());

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreated(),
                savedUser.getModified(),
                savedUser.getToken(),
                savedUser.getIsActive(),
                savedUser.getLastLogin(),
                phoneResponses
        );
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(user -> {
            List<PhoneResponse> phoneResponses = user.getPhones().stream()
                    .map(p -> new PhoneResponse(p.getNumber(), p.getCityCode(), p.getCountryCode()))
                    .collect(Collectors.toList());

            return new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getCreated(),
                    user.getModified(),
                    user.getToken(),
                    user.getIsActive(),
                    user.getLastLogin(),
                    phoneResponses
            );
        }).collect(Collectors.toList());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(Messages.USER_NOT_FOUND));

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException(Messages.USER_PASSWORD_REQUIRED);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException(Messages.USER_INVALID_PASSWORD);
        }

        // Actualizar token y último login
        String token = jwtUtil.generateToken(user.getEmail());
        user.setToken(token);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token,
                user.getLastLogin(),
                user.getIsActive()
        );
    }

}
